package ru.practicum.utils;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ValidationException;

import static ru.practicum.request.model.StateAction.PUBLISH_EVENT;
import static ru.practicum.request.model.StateAction.REJECT_EVENT;

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

    public void checkForUpdateEvent(UpdateEventUserRequest eventDto) {
        if (StringUtils.isBlank(eventDto.getDescription())) {
            throw new ValidationException("Поле с описанием обязательно к заполнению.");
        }
        if (StringUtils.isBlank(eventDto.getAnnotation())) {
            throw new ValidationException("Поле 'Аннотация' обязательно к заполению.");
        }
        if (eventDto.getEventDate() == null) {
            throw new ValidationException("Дата и время на которые намечено событие обязательны к заполению.");
        }
        if (eventDto.getTitle().length() < 3 || eventDto.getTitle().length() > 120) {
            throw new ValidationException("Заголовок должен иметь длину от 3 до 120 символов.");
        }
        if (eventDto.getDescription().length() < 20 || eventDto.getDescription().length() > 7000) {
            throw new ValidationException("Описание должно иметь длину от 20 до 7000 символов.");
        }
        if (eventDto.getAnnotation().length() < 20 || eventDto.getAnnotation().length() > 2000) {
            throw new ValidationException("Аннотация должна иметь длину от 20 до 2000 символов.");
        }
    }

    public boolean checkRestriction(Event event) {
        return !event.getRequestModeration() && event.getParticipantLimit() == 0;
    }

    public void validStateForUpdate(UpdateEventAdminRequest eventDto, Event event) {
        if (eventDto.getStateAction().equals(PUBLISH_EVENT) && (!event.getState().equals(State.PENDING))) {
            throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
        }

        if (eventDto.getStateAction().equals(REJECT_EVENT) && (!event.getState().equals(State.PUBLISHED))) {
            throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано.");
        }
    }
}
