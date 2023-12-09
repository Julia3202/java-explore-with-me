package ru.practicum.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.location.model.Location;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import static ru.practicum.validator.Constants.DATE_TIME_FORMATTER;

@UtilityClass
public class EventMapper {

    public Event toEvent(NewEventDto newEventDto, Category category, User user, Location location) {
        return new Event(
                null,
                newEventDto.getAnnotation(),
                category,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER),
                user,
                location,
                newEventDto.getPaid(),
                newEventDto.getParticipantLimit(),
                null,
                State.PENDING,
                newEventDto.getTitle(),
                newEventDto.getRequestModeration()
        );
    }

    public EventShortDto toEventShortDto(Event event, Integer confirmedRequests, Long views) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                confirmedRequests,
                event.getEventDate().format(DATE_TIME_FORMATTER),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                views
        );
    }
}
