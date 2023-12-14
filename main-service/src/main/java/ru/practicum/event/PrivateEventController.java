package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.PrivateEventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final PrivateEventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto create(@PathVariable Long userId, @RequestBody NewEventDto newEventDto) {
        return eventService.create(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId, @PathVariable Long eventId,
                               @RequestBody UpdateEventUserRequest eventDto) {
        return eventService.update(userId, eventId, eventDto);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestForOwner(@PathVariable Long userId, @PathVariable Long eventId,
                                                                @RequestBody EventRequestStatusUpdateRequest
                                                                        eventRequest) {
        return eventService.updateRequestsForOwnersEvent(userId, eventId, eventRequest);
    }

    @GetMapping
    public List<EventShortDto> getEvent(@PathVariable Long userId, @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getPrivateEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getOwnerEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getOwnerEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestForOwnerEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getRequestForOwnerEvent(userId, eventId);
    }
}
