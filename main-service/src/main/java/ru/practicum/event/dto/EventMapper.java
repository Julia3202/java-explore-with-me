package ru.practicum.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.location.dto.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.request.model.StateAction;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.validator.StateUserAction;

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

    public EventFullDto toEventFullDto(Event event, Integer confirmedRequest, Long views) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getCreatedOn().format(DATE_TIME_FORMATTER),
                event.getDescription(),
                event.getEventDate().format(DATE_TIME_FORMATTER),
                UserMapper.toUserShortDto(event.getInitiator()),
                LocationMapper.toLocationDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn().format(DATE_TIME_FORMATTER),
                event.getRequestModeration(),
                event.getState().toString(),
                event.getTitle(),
                views,
                confirmedRequest
        );
    }

    public Event toEventFromAdminUpdateDto(Event event, UpdateEventAdminRequest eventDto, Category category, Location location) {
        Event eventFromDto = Event.builder()
                .id(event.getId())
                .initiator(event.getInitiator())
                .annotation(eventDto.getAnnotation() != null ? eventDto.getAnnotation() : event.getAnnotation())
                .category(category != null ? category : event.getCategory())
                .description(eventDto.getDescription() != null ? eventDto.getDescription() : event.getDescription())
                .eventDate(eventDto.getEventDate() != null ? eventDto.getEventDate() : event.getEventDate())
                .location(location != null ? location : event.getLocation())
                .paid(eventDto.getPaid() != null ? eventDto.getPaid() : event.getPaid())
                .participantLimit(eventDto.getParticipantLimit() != null ? eventDto.getParticipantLimit() :
                        event.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration() != null ? eventDto.getRequestModeration() :
                        event.getRequestModeration())
                .title(eventDto.getTitle() != null ? eventDto.getTitle() : event.getTitle())
                .createdOn(event.getCreatedOn())
                .build();
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                eventFromDto.setState(State.PUBLISHED);
                eventFromDto.setPublishedOn(LocalDateTime.now());
            } else {
                eventFromDto.setState(State.CANCELED);
            }
        } else {
            eventFromDto.setState(event.getState());
        }
        return eventFromDto;
    }

    public Event toEventFromUserUpdateDto(Event event, UpdateEventUserRequest eventDto, Category category, Location location) {
        Event eventFromDto = Event.builder()
                .id(event.getId())
                .initiator(event.getInitiator())
                .annotation(eventDto.getAnnotation() != null ? eventDto.getAnnotation() : event.getAnnotation())
                .category(category != null ? category : event.getCategory())
                .description(eventDto.getDescription() != null ? eventDto.getDescription() : event.getDescription())
                .eventDate(eventDto.getEventDate() != null ? eventDto.getEventDate() : event.getEventDate())
                .location(location != null ? location : event.getLocation())
                .paid(eventDto.getPaid() != null ? eventDto.getPaid() : event.getPaid())
                .participantLimit(eventDto.getParticipantLimit() != null ? eventDto.getParticipantLimit() :
                        event.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration() != null ? eventDto.getRequestModeration() :
                        event.getRequestModeration())
                .title(eventDto.getTitle() != null ? eventDto.getTitle() : event.getTitle())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .build();
        if (eventDto.getStateAction() != null) {
            eventFromDto.setState(
                    eventDto.getStateAction().equals(StateUserAction.SEND_TO_REVIEW) ? State.PENDING : State.CANCELED);
        } else {
            eventFromDto.setState(event.getState());
        }
        return eventFromDto;
    }
}
