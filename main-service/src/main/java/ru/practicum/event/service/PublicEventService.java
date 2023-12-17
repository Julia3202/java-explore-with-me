package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    EventFullDto getPublicEvent(Long id, HttpServletRequest httpServletRequest);

    List<EventShortDto> getPublicEventList(String text, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           Boolean onlyAvailable, String sort, Integer from,
                                           Integer size, HttpServletRequest httpServletRequest);
}
