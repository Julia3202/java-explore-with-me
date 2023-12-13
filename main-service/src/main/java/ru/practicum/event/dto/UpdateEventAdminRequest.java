package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.request.model.StateAction;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UpdateEventAdminRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}
