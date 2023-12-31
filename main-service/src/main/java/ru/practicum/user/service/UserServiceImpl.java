package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.utils.ValidatorService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidatorService validatorService;

    @Override
    public UserDto saveUser(NewUserDto newUserDto) {
        if (userRepository.findByEmail(newUserDto.getEmail()) != null) {
            throw new ConflictException("Пользователь с email- " + newUserDto.getEmail() +
                    " уже зарегистрирован.");
        }
        User userFromDto = UserMapper.toNewUser(newUserDto);
        User user = userRepository.save(userFromDto);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUser(Set<Long> ids, Integer from, Integer size) {
        validatorService.validSizeAndFrom(from, size);
        Pageable page = PageRequest.of(from / size, size);
        Page<User> userPage = CollectionUtils.isEmpty(ids) ?
                userRepository.findAll(page) :
                userRepository.findAllByIdIn(page, ids);
        return userPage.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID- " + id + "не найден."));
        userRepository.delete(user);
    }
}
