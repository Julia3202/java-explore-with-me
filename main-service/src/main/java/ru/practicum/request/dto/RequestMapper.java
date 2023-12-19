package ru.practicum.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.request.model.Request;

@UtilityClass
public class RequestMapper {

    public ParticipationRequestDto toDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated() != null ? request.getCreated() : null,
                request.getEvent() != null ? request.getEvent().getId() : null,
                request.getRequester() != null ? request.getRequester().getId() : null,
                request.getStatus() != null ? request.getStatus() : null
        );
    }
}
