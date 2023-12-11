package ru.practicum.validator;

import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;

import static ru.practicum.request.model.StateAction.PUBLISH_EVENT;

public class EventValidator {
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
