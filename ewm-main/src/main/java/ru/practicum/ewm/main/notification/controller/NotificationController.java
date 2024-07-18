package ru.practicum.ewm.main.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.notification.dto.NotificationDto;
import ru.practicum.ewm.main.notification.service.NotificationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<NotificationDto> getUserNotifications(@PathVariable Long userId,
                                                      @RequestParam(required = false) Boolean read,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) String sortOrder) {
        return notificationService.getUserNotifications(userId, read, page, size, sortOrder);
    }

    @PostMapping("/read")
    public void markAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markAsRead(notificationIds);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }

    @DeleteMapping("/{followerId}/all")
    public void deleteAllNotificationsByFollower(@PathVariable Long followerId) {
        notificationService.deleteAllNotificationsByFollower(followerId);
    }

    @DeleteMapping("/{followerId}/following/{followingId}/all")
    public void deleteAllNotificationsByFollowerAndFollowing(@PathVariable Long followerId, @PathVariable Long followingId) {
        notificationService.deleteAllNotificationsByFollowerAndFollowing(followerId, followingId);
    }
}