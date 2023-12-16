package ru.practicum.utils;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;

import static ru.practicum.utils.Constants.DATE_TIME_FORMATTER;

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
        if ((start != null && end != null) && (start.isAfter(end))) {
            throw new ValidationException("Проверьте правильность заполнения полей с датами.");
        }
    }

    public void validStartForUpdate(LocalDateTime time) {
        if (time != null && time.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Нельзя вносить изменения в событие, т.к. до него осталось меньше часа.");
        }
    }
}
