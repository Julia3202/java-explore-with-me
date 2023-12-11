package ru.practicum.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.request.model.Request;

import static ru.practicum.validator.Constants.DATE_TIME_FORMATTER;

@UtilityClass
public class RequestMapper {

    public ParticipationRequestDto toDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated() != null ? request.getCreated().format(DATE_TIME_FORMATTER) : null,
                request.getEvent() != null ? request.getEvent().getId() : null,
                request.getRequester() != null ? request.getRequester().getId() : null,
                request.getStatus() != null ? request.getStatus().name() : null
        );
    }
}
