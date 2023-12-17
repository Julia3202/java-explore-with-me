package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatClient;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.exception.ConflictException;
import ru.practicum.location.dao.LocationRepository;
import ru.practicum.location.dto.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.utils.State;
import ru.practicum.utils.StateAction;
import ru.practicum.utils.ValidatorService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.event.dto.EventMapper.EVENT_MAPPER;
import static ru.practicum.utils.StateAction.PUBLISH_EVENT;
import static ru.practicum.utils.StateAction.REJECT_EVENT;
import static ru.practicum.utils.Status.CONFIRMED;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatClient statClient;
    private final ValidatorService validatorService;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAdminFullEvent(List<Long> users, List<State> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(1);
        }
        Pageable page = PageRequest.of(from / size, size);
        BooleanBuilder query = new BooleanBuilder()
                .and(!CollectionUtils.isEmpty(users) ? QEvent.event.initiator.id.in(users) : null)
                .and(!CollectionUtils.isEmpty(categories) ? QEvent.event.category.id.in(categories) : null)
                .and(QEvent.event.eventDate.goe(rangeStart))
                .and(QEvent.event.eventDate.loe(rangeEnd));
        if (!CollectionUtils.isEmpty(states)) {
            query.and(QEvent.event.state.in(states));
        }
        Page<Event> events = eventRepository.findAll(query, page);
        List<String> eventUrls = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(rangeStart, rangeEnd, eventUrls, true);
        return events.stream()
                .map(EVENT_MAPPER::toFullDto)
                .peek(eventFullDto -> {
                    Optional<ViewStatsDto> viewStatsDto = viewStatsDtos.stream()
                            .filter(viewStatsDto1 -> viewStatsDto1.getUri().equals("/events/" + eventFullDto.getId()))
                            .findFirst();
                    eventFullDto.setViews(viewStatsDto.map(ViewStatsDto::getHits).orElse(0L));
                }).peek(eventFullDto -> eventFullDto.setConfirmedRequests(
                        requestRepository.countByEventIdAndStatus(eventFullDto.getId(), CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateFromAdminEvent(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = validatorService.existEventById(eventId);
        if (eventDto.getEventDate() != null
                && LocalDateTime.now().plusHours(1).isAfter(eventDto.getEventDate())) {
            throw new ConflictException("Нельзя вносить изменения в событие, т.к. до него осталось меньше часа.");
        }
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(PUBLISH_EVENT.name())
                    && !event.getState().equals(State.PENDING)) {
                throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
            }
            if (eventDto.getStateAction().equals(REJECT_EVENT.name()) &&
                    event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано.");
            }
        }
        if (eventDto.getCategory() != null) {
            event.setCategory(validatorService.existCategoryById(eventDto.getCategory()));
        }
        if (eventDto.getLocation() != null) {
            Location location = locationRepository.save(LocationMapper.toLocation(eventDto.getLocation()));
            event.setLocation(location);
        }
        Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(eventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(eventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        if (eventDto.getStateAction() != null) {
            switch (StateAction.valueOf(eventDto.getStateAction())) {
                case PUBLISH_EVENT:
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(State.CANCELED);
                    break;
            }
        }
        return EVENT_MAPPER.toFullDto(eventRepository.save(event));
    }
}
