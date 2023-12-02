package ru.practicum.dto;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;

import static ru.practicum.model.Constants.DATE_TIME_FORMATTER;

public class EndpointHitMapper {

    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp() != null ? endpointHit.getTimestamp().format(DATE_TIME_FORMATTER) : null
        );
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp() != null ? LocalDateTime.parse(endpointHitDto.getTimestamp(),
                        DATE_TIME_FORMATTER) : null
        );
    }
}
