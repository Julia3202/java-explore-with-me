package ru.practicum.event.dto;

import ru.practicum.location.dto.LocationDto;

public class EventUpdate {
    private String annotation;
    private Long category;
    private String description;
    private LocationDto location;

    private Boolean paid;
    private Integer participantLimit;

    private Boolean requestModeration;
    private String title;
}
