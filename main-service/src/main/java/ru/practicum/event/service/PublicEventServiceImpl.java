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
import ru.practicum.client.StatClient;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.validator.DateValidator;
import ru.practicum.validator.ValidatorService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.validator.Constants.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final ValidatorService validatorService;
    private final EventUtilService eventUtilService;

    private final StatClient statClient;
    private final DateValidator dateValidator = new DateValidator();


    @Override
    public EventFullDto getPublicEvent(Long id, HttpServletRequest httpServletRequest) {
        Event event = validatorService.existEventById(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с ID-" + id + "не опубликовано.");
        }
        EventFullDto eventFullDto = eventUtilService.listEventFull(List.of(event)).get(0);
        EndpointHitDto endpointHitDto = new EndpointHitDto(null, "ewm-main", httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
        statClient.postHit(endpointHitDto);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getPublicEventList(String text, List<Long> categories, Boolean paid,
                                                  String rangeStart, String rangeEnd,
                                                  Boolean onlyAvailable, String sort, Integer from,
                                                  Integer size, HttpServletRequest httpServletRequest) {
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
        List<EventShortDto> shortDtos = eventUtilService.listEventShort(events.toList());

        EndpointHitDto endpointHitDto = new EndpointHitDto(null, "ewm-main", httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(), LocalDateTime.now().format(DATE_TIME_FORMATTER));
        statClient.postHit(endpointHitDto);
        return shortDtos;
    }


}