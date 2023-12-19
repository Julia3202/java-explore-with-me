package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.PrivateEventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {
    private final PrivateEventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto create(@PathVariable Long userId, @RequestBody NewEventDto newEventDto) {
        log.info("Поступил запрос на получение списка событий, добавленных пользователем с id={}", userId);
        return eventService.create(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId, @PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventUserRequest eventDto) {
        log.info("Поступил запрос на изменение события с id={} от пользователя с id={}, updateData={}",
                eventId, userId, eventDto);
        return eventService.update(userId, eventId, eventDto);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestForOwner(@PathVariable Long userId, @PathVariable Long eventId,
                                                                @Valid @RequestBody EventRequestStatusUpdateRequest
                                                                        eventRequest) {
        log.info("Поступил запрос на изменение статуса заявок на участие в событии с id={} " +
                "от пользователя с id={}, updateRequest={}", eventId, userId, eventRequest);
        return eventService.updateRequestsForOwnersEvent(userId, eventId, eventRequest);
    }

    @GetMapping
    public List<EventShortDto> getEvent(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос на получение списка событий, добавленных пользователем с id={}", userId);
        return eventService.getPrivateEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getOwnerEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Поступил запрос на получения списка запросов на участие в событии с id={}, " +
                "от пользователя с id={}", userId, eventId);
        return eventService.getOwnerEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestForOwnerEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Поступил запрос на получение информации о запросах на участие в событии с ID- {} пользователя с ID- {}",
                eventId, userId);
        return eventService.getRequestForOwnerEvent(userId, eventId);
    }
}
