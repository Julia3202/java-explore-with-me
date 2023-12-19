package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserDto> getUser(Set<Long> ids, Integer from, Integer size);

    UserDto saveUser(NewUserDto newUserDto);

    void delete(Long id);
}
