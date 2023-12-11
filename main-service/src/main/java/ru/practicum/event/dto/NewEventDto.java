package ru.practicum.event.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.location.dto.LocationDto;

@Getter
@Setter
@ToString
public class NewEventDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}
