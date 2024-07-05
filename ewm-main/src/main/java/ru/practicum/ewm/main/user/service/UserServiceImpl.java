package ru.practicum.ewm.main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.UserNotFoundException;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.dto.UserMapper;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            User savedUser = userRepository.save(UserMapper.toUser(userDto));
            return UserMapper.toUserDto(savedUser);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Integrity constraint has been violated.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(UserMapper::toUserDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users;
        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findAllById(ids).stream()
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        } else {
            users = userRepository.findAll(pageable).getContent();
        }
        return UserMapper.toUserDtoList(users);
    }

    @Override
    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }
}