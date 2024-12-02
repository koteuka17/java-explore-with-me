package ru.practicum.adminapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adminapi.user.service.AdminUserService;
import ru.practicum.entity.util.MyPageRequest;
import ru.practicum.entity.dto.user.NewUserRequest;
import ru.practicum.entity.dto.user.UserDto;
import ru.practicum.entity.exception.ConflictException;
import ru.practicum.entity.mapper.UserMapper;
import ru.practicum.entity.model.User;
import ru.practicum.entity.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        MyPageRequest pageable = new MyPageRequest(from, size, Sort.by(Sort.Direction.ASC, "id"));
        if (ids == null) {
            users = userRepository.findAll(pageable).toList();
        } else {
            users = userRepository.findAllByIdIn(ids, pageable);
        }
        log.info("Number of users: {}", users.size());
        return UserMapper.toUserDtoList(users);
    }

    @Transactional
    @Override
    public UserDto save(NewUserRequest dto) {
        User user = UserMapper.toEntity(dto);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Данный email уже занят", e);
        }
        log.info("Add user: {}", user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        if (userRepository.existsById(userId)) {
            log.info("Deleted user with id = {}", userId);
            userRepository.deleteById(userId);
        }
    }
}
