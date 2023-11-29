package ru.practicum.model;

import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;

public class TimeValidator {
    public void validTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Проверьте правильность заполнения полей с началом и окончанием периода. " +
                    "Окончание периода не должно быть раньше начала.");
        }
    }
}
