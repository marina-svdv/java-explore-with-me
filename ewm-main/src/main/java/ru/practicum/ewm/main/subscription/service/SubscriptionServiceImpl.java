package ru.practicum.ewm.main.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.UserNotFoundException;
import ru.practicum.ewm.main.notification.repository.NotificationRepository;
import ru.practicum.ewm.main.notification.service.NotificationService;
import ru.practicum.ewm.main.subscription.dto.SubscriptionDto;
import ru.practicum.ewm.main.subscription.dto.SubscriptionMapper;
import ru.practicum.ewm.main.subscription.model.Subscription;
import ru.practicum.ewm.main.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Override
    public SubscriptionDto follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("User cannot follow himself");
        }
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Follower with id= " + followerId + " not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new UserNotFoundException("Following with id= " + followingId + " user not found"));
        if (subscriptionRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new ConflictException("Already following");
        }

        String message = "You have a new follower: " + follower.getName();
        notificationService.createNotification(following, follower, message);

        Subscription subscription = new Subscription();
        subscription.setFollower(follower);
        subscription.setFollowing(following);
        subscription = subscriptionRepository.save(subscription);
        return SubscriptionMapper.toDto(subscription);
    }

    @Override
    public List<SubscriptionDto> getFollowing(Long userId, String name, String startDate, String endDate,
                                              int page, int size, String sortBy, String sortOrder) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id= " + userId + " not found"));

        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME) : LocalDateTime.now().minusYears(50);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME) : LocalDateTime.now();

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        String sortField = sortBy != null && (sortBy.equals("createdAt") || sortBy.equals("name")) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField);

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return subscriptionRepository.findAllByFollowerAndFilter(user, name, start, end, pageRequest)
                .stream()
                .map(SubscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionDto> getFollowers(Long userId, String name, String startDate, String endDate,
                                              int page, int size, String sortBy, String sortOrder) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id= " + userId + " not found"));

        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME) : LocalDateTime.now().minusYears(50);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME) : LocalDateTime.now();

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        String sortField = sortBy != null && (sortBy.equals("createdAt") || sortBy.equals("name")) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField);

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return subscriptionRepository.findAllByFollowingAndFilter(user, name, start, end, pageRequest)
                .stream()
                .map(SubscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void unfollow(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Follower with id= " + followerId + " not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new UserNotFoundException("Following with id= " + followingId + " user not found"));
        Subscription subscription = subscriptionRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new IllegalStateException("Follow relationship not found"));

        subscriptionRepository.delete(subscription);
        notificationRepository.deleteAllByUserAndFromUser(follower, following);
    }
}