package ru.practicum.ewm.main.subscription.dto;

import lombok.Data;

@Data
public class SubscriptionDto {
    private Long id;
    private Long followerId;
    private String followerName;
    private Long followingId;
    private String followingName;
}