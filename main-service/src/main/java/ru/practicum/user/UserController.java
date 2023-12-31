package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserDto newUserDto) {
        return userService.saveUser(newUserDto);
    }

    @GetMapping
    public List<UserDto> getUser(@RequestParam(required = false, name = "ids") Set<Long> ids,
                                 @Valid @RequestParam(defaultValue = "0") Integer from,
                                 @Valid @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUser(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Valid Long userId) {
        userService.delete(userId);
    }
}
