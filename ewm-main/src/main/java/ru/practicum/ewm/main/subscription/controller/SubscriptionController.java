package ru.practicum.ewm.main.subscription.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.subscription.dto.SubscriptionDto;
import ru.practicum.ewm.main.subscription.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}/follow/{followingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto follow(@PathVariable Long followerId,
                                  @PathVariable Long followingId) {
        return subscriptionService.follow(followerId, followingId);
    }

    @GetMapping("/{userId}/following")
    public List<SubscriptionDto> getFollowing(@PathVariable Long userId,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String startDate,
                                              @RequestParam(required = false) String endDate,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String sortBy,
                                              @RequestParam(defaultValue = "desc") String sortOrder) {
        return subscriptionService.getFollowing(userId, name, startDate, endDate, page, size, sortBy, sortOrder);
    }

    @GetMapping("/{userId}/followers")
    public List<SubscriptionDto> getFollowers(@PathVariable Long userId,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String startDate,
                                              @RequestParam(required = false) String endDate,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String sortBy,
                                              @RequestParam(defaultValue = "desc") String sortOrder) {
        return subscriptionService.getFollowers(userId, name, startDate, endDate, page, size, sortBy, sortOrder);
    }

    @DeleteMapping("/{followerId}/unfollow/{followingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(@PathVariable Long followerId,
                         @PathVariable Long followingId) {
        subscriptionService.unfollow(followerId, followingId);
    }
}