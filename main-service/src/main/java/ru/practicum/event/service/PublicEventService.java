package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicEventService {

    EventFullDto getPublicEvent(Long id, HttpServletRequest httpServletRequest);

    List<EventShortDto> getPublicEventList(String text, List<Long> categories, Boolean paid, String rangeStart,
                                           String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                           Integer size, HttpServletRequest httpServletRequest);
}
