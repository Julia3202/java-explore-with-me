package ru.practicum.validator;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ValidationException;

import static ru.practicum.request.model.StateAction.PUBLISH_EVENT;

public class EventValidator {
    public void checkForCreateEvent(NewEventDto newEventDto) {
        if (StringUtils.isEmpty(newEventDto.getDescription())) {
            throw new ValidationException("Поле с описанием обязательно к заполнению.");
        }
        if (StringUtils.isEmpty(newEventDto.getAnnotation())) {
            throw new ValidationException("Поле 'Аннотация' обязательно к заполению.");
        }
    }

    public boolean checkRestriction(Event event) {
        if (!event.getRequestModeration() && event.getParticipantLimit() == 0) {
            return true;
        }
        return false;
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
