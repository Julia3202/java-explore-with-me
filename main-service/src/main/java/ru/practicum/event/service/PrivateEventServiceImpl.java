package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.location.dao.LocationRepository;
import ru.practicum.location.dto.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.user.model.User;
import ru.practicum.validator.EventValidator;
import ru.practicum.validator.LocationValidator;
import ru.practicum.validator.ValidatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.event.model.State.PENDING;

@Service
@RequiredArgsConstructor
@Transactional
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final ValidatorService validatorService;
    private final LocationRepository locationRepository;
    private final EventUtilService eventUtilService;
    private final RequestRepository requestRepository;

    private final EventValidator eventValidator = new EventValidator();
    private final LocationValidator locationValidator = new LocationValidator();

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        eventValidator.checkForCreateEvent(newEventDto);
        User user = validatorService.existUserById(userId);
        Category category = validatorService.existCategoryById(newEventDto.getCategory());
        Location locations = LocationMapper.toLocation(newEventDto.getLocation());
        Location location = locationRepository.save(locations);
        Event event = EventMapper.toEvent(newEventDto, category, user, location);
        event.setInitiator(user);
        event.setState(PENDING);
        eventRepository.save(event);
        return EventMapper.toEventFullDto(event, 0, 0L);
    }

    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        Event event = validatorService.existEventById(eventId);
        User user = validatorService.existUserById(userId);
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("Пользователь с ID-" + userId + " не является создателем события.");
        }
        Category category = null;
        if (eventDto.getCategory() != null) {
            category = validatorService.existCategoryById(eventDto.getCategory());
        }
        Location location = null;
        if (eventDto.getLocation() != null) {
            locationValidator.validLocation(eventDto.getLocation().getLat(), eventDto.getLocation().getLon());
            location = locationRepository.save(LocationMapper.toLocation(eventDto.getLocation()));
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Изменять можно события, которые еще не опубликованы или отменены.");
        }
        Event eventFromRepository = eventRepository.save(EventMapper.toEventFromUserUpdateDto(event, eventDto, category,
                location));
        return eventUtilService.listEventFull(List.of(eventFromRepository)).get(0);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsForOwnersEvent(Long userId, Long eventId,
                                                                       EventRequestStatusUpdateRequest eventRequest) {
        Event event = validatorService.existEventById(eventId);
        User user = validatorService.existUserById(userId);
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("Пользователь с ID-" + userId + " не является создателем события.");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
            List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
            List<Request> requestsList = requestRepository.findAllByEventId(eventId);
            for (Request request : requestsList) {
                if (request.getStatus().equals(Status.REJECTED)) {
                    rejectedRequests.add(RequestMapper.toDto(request));
                } else if (request.getStatus().equals(Status.CONFIRMED)) {
                    confirmedRequests.add(RequestMapper.toDto(request));
                }
            }
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }
        Integer confirmRequests = requestRepository.countAllByStatusAndEventId(Status.CONFIRMED, event.getId());
        if (event.getParticipantLimit() <= confirmRequests) {
            throw new ConflictException("На событие с ID-" + eventId + " уже зарегистрировано максимально " +
                    "количество участников.");
        }
        List<Request> requestList = requestRepository.findAllByIdIn(eventRequest.getRequestIds());
        int counter = 0;
        int requestsToUpdate = event.getParticipantLimit() - confirmRequests;
        List<Long> requestsIdsForConfirm = new ArrayList<>(requestsToUpdate);
        List<Long> requestsIdsForReject = new ArrayList<>(confirmRequests);
        List<Request> confirmedRequestList = requestRepository.findAllByEventIdAndStatus(event.getId(), Status.CONFIRMED);
        List<Request> rejectRequestList = requestRepository.findAllByEventIdAndStatus(event.getId(), Status.REJECTED);
        for (Request request : requestList) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("У запроса с ID: " + request.getId() + "статус: " + request.getStatus() +
                        ", ожидалось PENDING");
            }
            if (!request.getEvent().equals(event)) {
                throw new ConflictException("Запрос с ID-" + request.getId() + " не относится к событию с ID-" +
                        event.getId());
            }
            if (eventRequest.getStatus().equals(Status.CONFIRMED.name()) && counter < requestsToUpdate) {
                requestsIdsForConfirm.add(request.getId());
                request.setStatus(Status.CONFIRMED);
                confirmedRequestList.add(request);
                counter++;
            } else {
                requestsIdsForReject.add(request.getId());
                request.setStatus(Status.REJECTED);
                rejectRequestList.add(request);
            }
        }
        requestRepository.requestStatusUpdate(Status.valueOf(eventRequest.getStatus()), requestsIdsForConfirm);
        if (!requestsIdsForReject.isEmpty()) {
            requestRepository.requestStatusUpdate(Status.REJECTED, requestsIdsForConfirm);
        }
        List<ParticipationRequestDto> confirmedRequests = confirmedRequestList.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = rejectRequestList.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPrivateEvents(Long userId, Integer from, Integer size) {
        validatorService.existUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return eventUtilService.listEventShort(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getOwnerEvent(Long userId, Long eventId) {
        Event event = validatorService.existEventById(eventId);
        User user = validatorService.existUserById(userId);
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("Пользователь с ID-" + userId + " не является создателем события.");
        }
        return eventUtilService.listEventFull(List.of(event)).get(0);
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
