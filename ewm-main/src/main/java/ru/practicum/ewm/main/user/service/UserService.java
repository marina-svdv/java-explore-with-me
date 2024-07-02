package ru.practicum.ewm.main.user.service;

import ru.practicum.ewm.main.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers(List<Long> ids, int from, int size);

    void deleteUser(Long id);
}