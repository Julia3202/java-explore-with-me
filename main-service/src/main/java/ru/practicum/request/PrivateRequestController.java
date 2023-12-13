package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PrivateRequestController {

    private final RequestService requestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ParticipationRequestDto postParticipantRequest(@PathVariable long userId,
                                                          @RequestParam long eventId) {
        log.info("Поступил запрос на добавление заявки на участие в событии с id={}, от пользователя с id={}",
                eventId, userId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto patchRequestCancel(@PathVariable long userId,
                                                      @PathVariable long requestId) {
        return requestService.update(userId, requestId);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUsersParticipantsRequests(@PathVariable long userId) {
        return requestService.get(userId);
    }
}
