package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatClient;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.utils.DateValidator;
import ru.practicum.utils.SorterEvent;
import ru.practicum.utils.ValidatorService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.event.dto.EventMapper.EVENT_MAPPER;
import static ru.practicum.request.model.Status.CONFIRMED;
import static ru.practicum.utils.Constants.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final ValidatorService validatorService;
    private final RequestRepository requestRepository;

    private final StatClient statClient;
    private final DateValidator dateValidator = new DateValidator();


    @Override
    public EventFullDto getPublicEvent(Long id, HttpServletRequest httpServletRequest) {
        Event event = validatorService.existEventById(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с ID-" + id + "не опубликовано.");
        }
        EventFullDto eventFullDto = EVENT_MAPPER.toFullDto(event);

        EndpointHitDto endpointHitDto = new EndpointHitDto(null, "ewm-main", httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(), dateValidator.toStringTime(LocalDateTime.now()));
        statClient.postHit(endpointHitDto);
        List<String> uriEvent = Collections.singletonList("/events/" + id);
        List<ViewStatsDto> viewStatsDtoList = statClient.getStats(LocalDateTime.now().minusYears(10),
                LocalDateTime.now().plusYears(10), uriEvent, true);
        eventFullDto.setViews(viewStatsDtoList.isEmpty() ? 0L : viewStatsDtoList.get(0).getHits());
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getPublicEventList(String text, List<Long> categories, Boolean paid,
                                                  String rangeStart, String rangeEnd,
                                                  Boolean onlyAvailable, SorterEvent sort, Integer from,
                                                  Integer size, HttpServletRequest httpServletRequest) {
        if (categories != null && categories.size() == 1 && categories.get(0).equals(0L)) {
            categories = null;
        }
        if (rangeStart == null) {
            rangeStart = dateValidator.toStringTime(LocalDateTime.now());
        }
        if (rangeEnd == null) {
            rangeEnd = dateValidator.toStringTime(LocalDateTime.now().plusYears(100));
        }
        LocalDateTime start = dateValidator.toTime(rangeStart);
        LocalDateTime end = dateValidator.toTime(rangeEnd);
        dateValidator.validTime(start, end);
        Pageable page = PageRequest.of(from / size, size);
        BooleanBuilder query = new BooleanBuilder()
                .and(text != null ? QEvent.event.annotation.containsIgnoreCase(text) : null)
                .or(text != null ? QEvent.event.description.containsIgnoreCase(text) : null)
                .and(QEvent.event.state.eq(State.PUBLISHED))
                .and(!CollectionUtils.isEmpty(categories) ? QEvent.event.category.id.in(categories) : null)
                .and(paid != null ? QEvent.event.paid.eq(paid) : null)
                .and((end == null && start == null) ?
                        QEvent.event.eventDate.after(LocalDateTime.now()) :
                        QEvent.event.eventDate.between(start, end));
        if (onlyAvailable) {
            query.and(QEvent.event.participantLimit.goe(0));
        }
        Page<Event> events = eventRepository.findAll(query, page);
        List<String> eventUrls = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(start, end, eventUrls, true);
        List<EventShortDto> shortDtos = events.stream()
                .map(EVENT_MAPPER::toShortDto)
                .peek(eventShortDto -> {
                    Optional<ViewStatsDto> viewStatsDto = viewStatsDtos.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + eventShortDto.getId()))
                            .findFirst();
                    eventShortDto.setViews(viewStatsDto.map(ViewStatsDto::getHits).orElse(0L));
                }).peek(dto -> dto.setConfirmedRequests(
                        requestRepository.countByEventIdAndStatus(dto.getId(), CONFIRMED)))
                .collect(Collectors.toList());
        switch (sort) {
            case EVENT_DATE:
                shortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
                break;
            case VIEWS:
                shortDtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
                break;
        }
        EndpointHitDto endpointHitDto = new EndpointHitDto(null, "ewm-main", httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
        statClient.postHit(endpointHitDto);
        return shortDtos;
    }


}