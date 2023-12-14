package ru.practicum.utils;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;

import static ru.practicum.request.model.StateAction.PUBLISH_EVENT;
import static ru.practicum.utils.Constants.DATE_TIME_FORMATTER;

public class EventValidator {
    public void checkForCreateEvent(NewEventDto newEventDto) {
        if (StringUtils.isBlank(newEventDto.getDescription())) {
            throw new ValidationException("Поле с описанием обязательно к заполнению.");
        }
        if (StringUtils.isBlank(newEventDto.getAnnotation())) {
            throw new ValidationException("Поле 'Аннотация' обязательно к заполению.");
        }
        LocalDateTime eventDateTime = LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER);
        if (LocalDateTime.now().plusHours(2).isBefore(eventDateTime)) {
            throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, чем через " +
                    "два часа от текущего момента.");
        }
        if (newEventDto.getTitle().length() < 3 || newEventDto.getTitle().length() > 120) {
            throw new ValidationException("Заголовок должен иметь длину от 3 до 120 символов.");
        }
        if (newEventDto.getDescription().length() < 20 || newEventDto.getDescription().length() > 7000) {
            throw new ValidationException("Заголовок должен иметь длину от 3 до 120 символов.");
        }
        if (newEventDto.getAnnotation().length() < 20 || newEventDto.getAnnotation().length() > 2000) {
            throw new ValidationException("Заголовок должен иметь длину от 3 до 120 символов.");
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getRequestModeration()) {
            newEventDto.setRequestModeration(true);
        }
    }

    public boolean checkRestriction(Event event) {
        return !event.getRequestModeration() && event.getParticipantLimit() == 0;
    }

    public void validStateForUpdate(UpdateEventAdminRequest eventDto, Event event) {
        if (eventDto.getStateAction().equals(PUBLISH_EVENT)) {
            if (!event.getState().equals(State.PENDING)) {
                throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
            }
        } else {
            if (!event.getState().equals(State.PENDING)) {
                throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано.");
            }
        }
    }
}
