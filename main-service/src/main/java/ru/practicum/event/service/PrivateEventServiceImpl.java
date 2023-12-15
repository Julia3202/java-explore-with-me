package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.dao.LocationRepository;
import ru.practicum.location.dto.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.user.model.User;
import ru.practicum.utils.DateValidator;
import ru.practicum.utils.EventValidator;
import ru.practicum.utils.LocationValidator;
import ru.practicum.utils.ValidatorService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.event.dto.EventMapper.EVENT_MAPPER;
import static ru.practicum.event.model.State.CANCELED;
import static ru.practicum.event.model.State.PENDING;
import static ru.practicum.request.model.Status.CONFIRMED;
import static ru.practicum.request.model.Status.REJECTED;
import static ru.practicum.utils.Constants.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final ValidatorService validatorService;
    private final LocationRepository locationRepository;
    private final EventUtilService eventUtilService;
    private final RequestRepository requestRepository;
    private final DateValidator dateValidator = new DateValidator();

    private final EventValidator eventValidator = new EventValidator();
    private final LocationValidator locationValidator = new LocationValidator();

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        eventValidator.checkForCreateEvent(newEventDto);
        dateValidator.validStartForUpdate(dateValidator.toTime(newEventDto.getEventDate()));
        User user = validatorService.existUserById(userId);
        Category category = validatorService.existCategoryById(newEventDto.getCategory());
        log.info("get locations");
        Location locations = LocationMapper.toLocation(newEventDto.getLocation());
        log.info("save location");
        Location location = locationRepository.save(locations);
        Event event = EVENT_MAPPER.fromDto(newEventDto, category, location);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setPaid(newEventDto.getPaid() != null && newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration());
        event.setState(PENDING);
        eventRepository.save(event);
        log.info("save event for repository");
        return EVENT_MAPPER.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        Event event = validatorService.existEventById(eventId);
        User user = validatorService.existUserById(userId);
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("Пользователь с ID-" + userId + " не является создателем события.");
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Изменять можно события, которые еще не опубликованы или отменены.");
        }
        LocalDateTime eventDateTime = LocalDateTime.parse(eventDto.getEventDate(), DATE_TIME_FORMATTER);
        if (LocalDateTime.now().plusHours(2).isAfter(eventDateTime)) {
            throw new ConflictException("Дата и время на которые намечено событие не может быть раньше, чем " +
                    "через два часа от текущего момента.");
        }
        if (!(event.getState().equals(CANCELED) ||
                event.getState().equals(PENDING))) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (eventDto.getCategory() != null) {
            event.setCategory(validatorService.existCategoryById(eventDto.getCategory()));
        }
        if (eventDto.getLocation() != null) {
            Location locations = LocationMapper.toLocation(eventDto.getLocation());
            locationValidator.validLocation(locations.getLat(), locations.getLon());
            Location location = locationRepository.save(locations);
            event.setLocation(location);
        }

        Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(eventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventDateTime).ifPresent(event::setEventDate);
        Optional.ofNullable(eventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventDto.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
            }
        }
        return EVENT_MAPPER.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsForOwnersEvent(Long userId, Long eventId,
                                                                       EventRequestStatusUpdateRequest eventRequest) {
        Event event = validatorService.existEventById(eventId);
        validatorService.existUserById(userId);
        long confirmedReq = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedReq) {
            throw new ConflictException("нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие.");
        }
        List<Request> requestList = requestRepository
                .findAllByIdIn(eventRequest.getRequestIds());
        List<Long> notFoundIds = eventRequest.getRequestIds().stream()
                .filter(requestId -> requestList.stream().noneMatch(request -> request.getId().equals(requestId)))
                .collect(Collectors.toList());
        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Participation request ids=" + notFoundIds + " not found");
        }
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();
        for (Request request : requestList) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Unable to accept/reject request that not is in pending state.");
            }
            if (!request.getEvent().getId().equals(eventId)) {
                result.getRejectedRequests().add(RequestMapper.toDto(request));
                continue;
            }
            switch (Status.valueOf(eventRequest.getStatus())) {
                case CONFIRMED:
                    if (confirmedReq < event.getParticipantLimit()) {
                        request.setStatus(CONFIRMED);
                        confirmedReq++;
                        result.getConfirmedRequests().add(RequestMapper.toDto(request));
                    } else {
                        request.setStatus(REJECTED);
                        result.getRejectedRequests().add(RequestMapper.toDto(request));
                        throw new ConflictException("Participant limit has reached.");
                    }
                    break;
                case REJECTED:
                    request.setStatus(REJECTED);
                    result.getRejectedRequests().add(RequestMapper.toDto(request));
                    break;
            }
        }
        requestRepository.saveAll(requestList);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPrivateEvents(Long userId, Integer from, Integer size) {
        validatorService.existUserById(userId);
        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = 10;
        }
        validatorService.validSizeAndFrom(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getOwnerEvent(Long userId, Long eventId) {
        Event event = validatorService.existEventById(eventId);
        User user = validatorService.existUserById(userId);
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("Пользователь с ID-" + userId + " не является создателем события.");
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestForOwnerEvent(Long userId, Long eventId) {
        Event event = validatorService.existEventById(eventId);
        User user = validatorService.existUserById(userId);
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("Пользователь с ID-" + userId + " не является создателем события.");
        }
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
