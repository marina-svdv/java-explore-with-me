package ru.practicum.ewm.main.subscription.service;

import ru.practicum.ewm.main.subscription.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDto follow(Long followerId, Long followingId);

    List<SubscriptionDto> getFollowing(Long userId, String name, String startDate, String endDate,
                                       int page, int size, String sortBy, String sortOrder);

    List<SubscriptionDto> getFollowers(Long userId, String name, String startDate, String endDate,
                                       int page, int size, String sortBy, String sortOrder);

    void unfollow(Long followerId, Long followingId);
}