package ru.practicum.event.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.location.model.Location;
@Getter
@Setter
@ToString
public class NewEventDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}
