package ru.practicum.ewm.main.notification.service;

import ru.practicum.ewm.main.notification.dto.NotificationDto;
import ru.practicum.ewm.main.user.model.User;

import java.util.List;

public interface NotificationService {

    void createNotification(User user, User fromUser, String message);

    List<NotificationDto> getUserNotifications(Long userId, Boolean read, int page, int size, String sortBy);

    public void markAsRead(List<Long> notificationIds);

    void deleteNotification(Long notificationId);

    void deleteAllNotifications(Long userId);
}