package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.request.model.StateAction;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UpdateEventAdminRequest extends UpdateEvent {
    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}
