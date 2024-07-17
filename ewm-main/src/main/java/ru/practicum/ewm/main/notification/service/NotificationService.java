package ru.practicum.ewm.main.notification.service;

import ru.practicum.ewm.main.notification.dto.NotificationDto;
import ru.practicum.ewm.main.user.model.User;

import java.util.List;

public interface NotificationService {

    void createNotification(User follower, User following, String message);

    List<NotificationDto> getUserNotifications(Long follower, Boolean read, int page, int size, String sortBy);

    void markAsRead(List<Long> notificationIds);

    void deleteNotification(Long notificationId);

    void deleteAllNotificationsByFollower(Long followerId);

    void deleteAllNotificationsByFollowerAndFollowing(Long followerId, Long followingId);
}