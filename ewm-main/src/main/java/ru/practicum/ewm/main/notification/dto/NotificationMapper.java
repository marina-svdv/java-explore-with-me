package ru.practicum.ewm.main.notification.dto;

import ru.practicum.ewm.main.notification.model.Notification;

public class NotificationMapper {

    public static NotificationDto toDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setMessage(notification.getMessage());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setRead(notification.isRead());
        return dto;
    }
}