package ru.practicum.ewm.main.subscription.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.subscription.model.Subscription;
import ru.practicum.ewm.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("SELECT s FROM Subscription s WHERE s.follower = :follower " +
            "AND (:name IS NULL OR s.following.name LIKE %:name%) " +
            "AND (s.createdAt >= :startDate) " +
            "AND (s.createdAt <= :endDate)")
    Page<Subscription> findAllByFollowerAndFilter(@Param("follower") User follower,
                                                  @Param("name") String name,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate,
                                                  Pageable pageable);

    @Query("SELECT s FROM Subscription s WHERE s.following = :following " +
            "AND (:name IS NULL OR s.follower.name LIKE %:name%) " +
            "AND (s.createdAt >= :startDate) " +
            "AND (s.createdAt <= :endDate)")
    Page<Subscription> findAllByFollowingAndFilter(@Param("following") User following,
                                                   @Param("name") String name,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate,
                                                   Pageable pageable);

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Subscription> findByFollowerAndFollowing(User follower, User following);

    List<Subscription> findAllByFollowing(User following);
}