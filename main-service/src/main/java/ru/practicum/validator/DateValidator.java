package ru.practicum.validator;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;

import static ru.practicum.validator.Constants.DATE_TIME_FORMATTER;

public class DateValidator {
    public LocalDateTime toTime(String time) {
        if (StringUtils.isEmpty(time)) {
            return null;
        }
        return LocalDateTime.parse(time, DATE_TIME_FORMATTER);
    }

    public String toStringTime(LocalDateTime time) {
        return time.format(DATE_TIME_FORMATTER);
    }

    public void validTime(LocalDateTime start, LocalDateTime end) {
        if ((start != null && end != null) && start.isAfter(end)) {
            throw new ValidationException("Проверьте правильность заполнения полей с датами.");
        }
    }

    public void validStartForUpdate(LocalDateTime time) {
        if (time != null && time.isAfter(LocalDateTime.now().minusHours(1))) {
            throw new ConflictException("Нельзя вносить изменения в событие, т.к. до него осталось меньше часа.");
        }
    }
}