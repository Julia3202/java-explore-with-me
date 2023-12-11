package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/request")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ParticipationRequestDto postParticipantRequest(@PathVariable long userId,
                                                          @RequestParam long eventId) {
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto patchRequestCancel(@PathVariable long userId,
                                                      @PathVariable long requestId) {
        return requestService.update(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getUsersParticipantsRequests(@PathVariable long userId) {
        return requestService.get(userId);
    }
}
