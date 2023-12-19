package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    EventRequestStatusUpdateResult updateRequestsForOwnersEvent(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequest);

    List<EventShortDto> getPrivateEvents(Long userId, Integer from, Integer size);

    EventFullDto getOwnerEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestForOwnerEvent(Long userId, Long eventId);
}
