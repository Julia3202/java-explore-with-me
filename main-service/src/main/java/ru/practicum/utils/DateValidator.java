package ru.practicum.utils;

import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;

import static ru.practicum.utils.Constants.DATE_TIME_FORMATTER;

public class DateValidator {
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
