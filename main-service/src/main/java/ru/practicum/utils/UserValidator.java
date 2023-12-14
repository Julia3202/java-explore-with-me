package ru.practicum.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.dto.NewUserDto;

@Slf4j
public class UserValidator {
    public boolean validName(NewUserDto user) {
        if (user.getName().length() > 250) {
            throw new ValidationException("Email не может быть длянее 254 символов.");
        }
        if (StringUtils.isBlank(user.getName())) {
            log.info("Поле с именем должно быть заполнено.");
            throw new ValidationException("Заполните поле с именем.");
        }
        return true;
    }

    public boolean validEmail(NewUserDto user) throws ValidationException {
        if ((user.getEmail() == null) || (!user.getEmail().contains("@"))) {
            log.warn("Поле 'email' не может быть пустым и должен содержать символ '@'.");
            throw new ValidationException("Поле 'email' не может быть пустым и должен содержать символ '@'.");
        }
        if (user.getEmail().length() > 254) {
            throw new ValidationException("Email не может быть длянее 254 символов.");
        }
        return true;
    }

    public void validate(NewUserDto user) {
        validName(user);
        validEmail(user);
    }
}
