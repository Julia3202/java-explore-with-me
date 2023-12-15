package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatClient;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.location.dao.LocationRepository;
import ru.practicum.location.dto.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.model.StateAction;
import ru.practicum.utils.DateValidator;
import ru.practicum.utils.EventValidator;
import ru.practicum.utils.LocationValidator;
import ru.practicum.utils.ValidatorService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.event.dto.EventMapper.EVENT_MAPPER;
import static ru.practicum.request.model.Status.CONFIRMED;
import static ru.practicum.utils.Constants.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final ValidatorService validatorService;
    private final LocationRepository locationRepository;
    private final LocationValidator locationValidator = new LocationValidator();
    private final DateValidator dateValidator = new DateValidator();
    private final EventValidator eventValidator = new EventValidator();
    private final StatClient statClient;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAdminFullEvent(List<Long> users, List<String> states, List<Long> categories,
                                                String rangeStart, String rangeEnd, Integer from, Integer size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusWeeks(4).format(DATE_TIME_FORMATTER);
        }
        LocalDateTime start = dateValidator.toTime(rangeStart);
        LocalDateTime end = dateValidator.toTime(rangeEnd);
        dateValidator.validTime(start, end);
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, page);
        List<String> eventUrls = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(start, end, eventUrls, true);
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
    public EventFullDto updateFromAdminEvent(long eventId, UpdateEventAdminRequest eventDto) {
        Event event = validatorService.existEventById(eventId);
        validatorService.existCategoryById(eventDto.getCategory());
        dateValidator.validStartForUpdate(event.getEventDate());
        if (eventDto.getStateAction() != null) {
            eventValidator.validStateForUpdate(eventDto, event);
        }
        if (eventDto.getCategory() != null) {
            event.setCategory(validatorService.existCategoryById(eventDto.getCategory()));
        }
        if (eventDto.getLocation() != null) {
            locationValidator.validLocation(eventDto.getLocation().getLat(), eventDto.getLocation().getLon());
            Location location = locationRepository.save(LocationMapper.toLocation(eventDto.getLocation()));
            event.setLocation(location);
        }
        //check location
        Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(eventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(dateValidator.toTime(eventDto.getEventDate())).ifPresent(event::setEventDate);
        Optional.ofNullable(eventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (eventDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }
        Event eventFromRepository = eventRepository.save(event);
        return EVENT_MAPPER.toFullDto(eventFromRepository);
    }
}
