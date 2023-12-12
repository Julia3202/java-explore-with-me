package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.user.model.User;
import ru.practicum.validator.EventValidator;
import ru.practicum.validator.RequestValidator;
import ru.practicum.validator.ValidatorService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ValidatorService validatorService;
    private final EventValidator eventValidator = new EventValidator();
    private final RequestValidator requestValidator = new RequestValidator();

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = validatorService.existUserById(userId);
        Event event = validatorService.existEventById(eventId);
        Optional<Request> requestList = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
        if (requestList.isPresent()) {
            throw new ConflictException("Вы уже зарегистрированны на данное меропрятие.");
        }
        requestValidator.validRequester(userId, event);
        if (event.getPublishedOn() == null) {
            throw new ConflictException("Мероятие не зарегистрировано.");
        }
        Integer participantsNumber = requestRepository.countAllByStatusAndEventId(Status.CONFIRMED, eventId);
        if (participantsNumber != null && participantsNumber >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictException("На мероприятие с ID: " + eventId + ", уже зарегистрировано максимальное кол-во участников");
        }
        Request request = new Request(null, LocalDateTime.now(), event, user, Status.PENDING);
        if (eventValidator.checkRestriction(event)) {
            request.setStatus(Status.CONFIRMED);
        }
        Request requestFromRepository = requestRepository.save(request);
        return RequestMapper.toDto(requestFromRepository);
    }

    @Override
    public ParticipationRequestDto update(Long userId, Long requestId) {
        User user = validatorService.existUserById(userId);
        Request request = validatorService.existRequestById(requestId);
        requestValidator.existRequester(request, user);
        request.setStatus(Status.CANCELED);
        Request requestFromRepository = requestRepository.save(request);
        return RequestMapper.toDto(requestFromRepository);
    }

    @Override
    public List<ParticipationRequestDto> get(Long userId) {
        validatorService.existUserById(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
