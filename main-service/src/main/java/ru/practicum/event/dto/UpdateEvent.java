package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.location.dto.LocationDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateEvent {

    private String annotation;
    private Long category;
    private String description;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}
