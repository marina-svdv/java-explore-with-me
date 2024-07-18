package ru.practicum.ewm.main.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.notification.model.Notification;
import ru.practicum.ewm.main.user.model.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByFollower(User follower, Pageable pageable);

    Page<Notification> findByFollowerAndRead(User follower, Boolean read, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.follower = :follower")
    void deleteAllByFollower(User follower);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.follower = :follower AND n.following = :following")
    void deleteAllByFollowerAndFollowing(@Param("follower") User follower,
                                      @Param("following") User following);
}