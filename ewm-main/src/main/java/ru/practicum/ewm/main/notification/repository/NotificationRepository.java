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

    Page<Notification> findByUser(User user, Pageable pageable);

    Page<Notification> findByUserAndRead(User user, Boolean read, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user = :user")
    void deleteAllByUser(User user);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user = :user AND n.fromUser = :fromUser")
    void deleteAllByUserAndFromUser(@Param("user") User user,
                                    @Param("fromUser") User fromUser);
}