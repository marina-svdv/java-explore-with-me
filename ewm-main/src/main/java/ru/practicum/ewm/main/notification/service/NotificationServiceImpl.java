package ru.practicum.ewm.main.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exception.UserNotFoundException;
import ru.practicum.ewm.main.notification.dto.NotificationDto;
import ru.practicum.ewm.main.notification.dto.NotificationMapper;
import ru.practicum.ewm.main.notification.model.Notification;
import ru.practicum.ewm.main.notification.repository.NotificationRepository;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void createNotification(User follower, User following, String message) {
        log.info("Creating notification for user with id={}", follower.getId());
        Notification notification = new Notification();
        notification.setFollower(follower);
        notification.setFollowing(following);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
        log.info("Notification created for user with id={}", follower.getId());
    }

    @Override
    public List<NotificationDto> getUserNotifications(Long userId, Boolean read, int page, int size, String sortOrder) {
        log.info("Fetching notifications for user with id={}, read={}, page={}, size={}, sortOrder={}",
                userId, read, page, size, sortOrder);
        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + userId + " not found"));

        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        List<Notification> notifications;
        if (read != null) {
            notifications = notificationRepository.findByFollowerAndRead(follower, read, pageRequest).getContent();
        } else {
            notifications = notificationRepository.findByFollower(follower, pageRequest).getContent();
        }

        log.info("Fetched {} notifications for user with id={}", notifications.size(), userId);
        return notifications.stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(List<Long> notificationIds) {
        log.info("Marking notifications as read: {}", notificationIds);
        if (notificationIds.isEmpty()) {
            log.info("No notifications to mark as read");
            return;
        }
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
        log.info("Marked {} notifications as read", notifications.size());
    }

    @Override
    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification with id={}", notificationId);
        notificationRepository.deleteById(notificationId);
        log.info("Deleted notification with id={}", notificationId);
    }

    @Override
    public void deleteAllNotificationsByFollower(Long followerId) {
        log.info("Deleting all notifications for follower with id={}", followerId);
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + followerId + " not found"));
        notificationRepository.deleteAllByFollower(follower);
        log.info("Deleted all notifications for follower with id={}", followerId);
    }

    @Override
    public void deleteAllNotificationsByFollowerAndFollowing(Long followerId, Long followingId) {
        log.info("Deleting all notifications for follower with id={} and following with id={}", followerId, followingId);
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + followerId + " not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + followingId + " not found"));
        notificationRepository.deleteAllByFollowerAndFollowing(follower, following);
        log.info("Deleted all notifications for follower with id={} and following with id={}", followerId, followingId);
    }
}