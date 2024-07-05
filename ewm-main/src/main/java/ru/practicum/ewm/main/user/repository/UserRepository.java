package ru.practicum.ewm.main.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}