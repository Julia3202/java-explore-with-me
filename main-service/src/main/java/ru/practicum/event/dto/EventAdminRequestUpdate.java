package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.request.model.StateAction;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class EventAdminRequestUpdate {
    private String annotation;
    private Long category;
    private String description;
    private LocationDto location;

    private Boolean paid;
    private Integer participantLimit;

    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}
