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
import ru.practicum.utils.StateUserAction;

import java.time.LocalDateTime;

import static ru.practicum.utils.Constants.DATE_TIME_FORMATTER;

@UtilityClass
public class EventMapper {

    public Event toEvent(NewEventDto newEventDto, Category category, User user, Location location) {
        return Event.builder()
                .id(0L)
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate() != null ? LocalDateTime.parse(newEventDto.getEventDate(),
                        DATE_TIME_FORMATTER) : null)
                .initiator(user)
                .location(location)
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false)
                .participantLimit(newEventDto.getParticipantLimit())
                .publishedOn(null)
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .requestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true)
                .build();

    }

    public EventShortDto toEventShortDto(Event event, Integer confirmedRequests, Long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory() != null ? CategoryMapper.toCategoryDto(event.getCategory()) : null)
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate() != null ? event.getEventDate().format(DATE_TIME_FORMATTER) : null)
                .initiator(event.getInitiator() != null ? UserMapper.toUserShortDto(event.getInitiator()) : null)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public EventFullDto toEventFullDto(Event event, Integer confirmedRequest, Long views) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn().format(DATE_TIME_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn().format(DATE_TIME_FORMATTER))
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(views)
                .confirmedRequests(confirmedRequest)
                .build();
    }

    public Event toEventFromAdminUpdateDto(Event event, UpdateEventAdminRequest eventDto, Category category, Location location) {
        Event eventFromDto = Event.builder()
                .id(event.getId())
                .initiator(event.getInitiator())
                .annotation(eventDto.getAnnotation() != null ? eventDto.getAnnotation() : event.getAnnotation())
                .category(category != null ? category : event.getCategory())
                .description(eventDto.getDescription() != null ? eventDto.getDescription() : event.getDescription())
                .eventDate(eventDto.getEventDate() != null ? LocalDateTime.parse(eventDto.getEventDate(),
                        DATE_TIME_FORMATTER) : event.getEventDate())
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
                .eventDate(eventDto.getEventDate() != null ? LocalDateTime.parse(eventDto.getEventDate(),
                        DATE_TIME_FORMATTER) : event.getEventDate())
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
