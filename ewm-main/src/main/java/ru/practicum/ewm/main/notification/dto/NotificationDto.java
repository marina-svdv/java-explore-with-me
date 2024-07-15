package ru.practicum.ewm.main.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private Long userId;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;
}