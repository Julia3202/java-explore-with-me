package ru.practicum.utils;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.exception.ValidationException;

public class EventValidator {
    public void checkForCreateEvent(NewEventDto newEventDto) {
        if (StringUtils.isBlank(newEventDto.getDescription())) {
            throw new ValidationException("Поле с описанием обязательно к заполнению.");
        }
        if (StringUtils.isBlank(newEventDto.getAnnotation())) {
            throw new ValidationException("Поле 'Аннотация' обязательно к заполению.");
        }
        if (newEventDto.getEventDate() == null) {
            throw new ValidationException("Дата и время на которые намечено событие обязательны к заполению.");
        }
        if (newEventDto.getTitle().length() < 3 || newEventDto.getTitle().length() > 120) {
            throw new ValidationException("Заголовок должен иметь длину от 3 до 120 символов.");
        }
        if (newEventDto.getDescription().length() < 20 || newEventDto.getDescription().length() > 7000) {
            throw new ValidationException("Описание должно иметь длину от 20 до 7000 символов.");
        }
        if (newEventDto.getAnnotation().length() < 20 || newEventDto.getAnnotation().length() > 2000) {
            throw new ValidationException("Аннотация должна иметь длину от 20 до 2000 символов.");
        }
    }
}
