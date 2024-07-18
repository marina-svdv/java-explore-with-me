package ru.practicum.ewm.main.subscription.dto;

import ru.practicum.ewm.main.subscription.model.Subscription;

public class SubscriptionMapper {

    public static SubscriptionDto toDto(Subscription subscription) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(subscription.getId());
        dto.setFollowerId(subscription.getFollower().getId());
        dto.setFollowerName(subscription.getFollower().getName());
        dto.setFollowingId(subscription.getFollowing().getId());
        dto.setFollowingName(subscription.getFollowing().getName());
        return dto;
    }
}