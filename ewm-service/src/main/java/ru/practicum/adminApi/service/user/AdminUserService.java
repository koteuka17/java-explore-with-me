package ru.practicum.adminApi.service.user;

import ru.practicum.entity.dto.user.NewUserRequest;
import ru.practicum.entity.dto.user.UserDto;

import java.util.List;

public interface AdminUserService {
    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    UserDto save(NewUserRequest newUserRequest);

    void delete(Long userId);
}
