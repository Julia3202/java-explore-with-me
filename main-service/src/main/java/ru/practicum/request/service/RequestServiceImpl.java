package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.user.model.User;
import ru.practicum.utils.EventValidator;
import ru.practicum.utils.RequestValidator;
import ru.practicum.utils.ValidatorService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.event.model.State.PUBLISHED;
import static ru.practicum.request.model.Status.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ValidatorService validatorService;
    private final EventValidator eventValidator = new EventValidator();
    private final RequestValidator requestValidator = new RequestValidator();

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = validatorService.existUserById(userId);
        Event event = validatorService.existEventById(eventId);
        if (event.getInitiator().equals(user)) {
            throw new ConflictException("The event initiator cannot apply to participate in his own event.");
        }
        if (!event.getState().equals(PUBLISHED)) {
            throw new ConflictException("Unable to participate in an unpublished event.");
        }
        Integer participantsNumber = requestRepository.countAllByStatusAndEventId(Status.CONFIRMED, eventId);

        if (participantsNumber != null && participantsNumber >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictException("На мероприятие с ID: " + eventId + ", уже зарегистрировано максимальное кол-во участников");
        }
        Optional<Request> optionalEvent = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (optionalEvent.isPresent()) {
            throw new ConflictException("Запрос от пользователя с ID: " + userId +
                    ", на мероприятие с ID: " + eventId + "уже зарегистрирован");
        }
        Request participationRequest = new Request();
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setEvent(event);
        participationRequest.setRequester(user);
        participationRequest.setStatus(
                event.getRequestModeration() && !event.getParticipantLimit().equals(0) ? PENDING : CONFIRMED);
        Request requestFromRepository = requestRepository.save(participationRequest);
        return RequestMapper.toDto(requestFromRepository);
    }

    @Override
    public ParticipationRequestDto update(Long userId, Long requestId) {
        User user = validatorService.existUserById(userId);
        Request request = validatorService.existRequestById(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request id=" + requestId + " not found.");
        }
        if (request.getStatus().equals(CONFIRMED)) {
            throw new ConflictException("Unable to cancel confirmed request.");
        }
        request.setStatus(CANCELED);
        Request requestFromRepository = requestRepository.save(request);
        return RequestMapper.toDto(requestFromRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> get(Long userId) {
        validatorService.existUserById(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
