package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.category.model.Category;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.State;
import ru.practicum.location.dao.LocationRepository;
import ru.practicum.location.dto.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.validator.DateValidator;
import ru.practicum.validator.EventValidator;
import ru.practicum.validator.LocationValidator;
import ru.practicum.validator.ValidatorService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;

    private final EventUtilService eventUtilService;
    private final ValidatorService validatorService;
    private final LocationRepository locationRepository;
    private final LocationValidator locationValidator = new LocationValidator();
    private final DateValidator dateValidator = new DateValidator();
    private final EventValidator eventValidator = new EventValidator();

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAdminFullEvent(List<Long> users, List<String> states, List<Long> categories,
                                                String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime start = dateValidator.toTime(rangeStart);
        LocalDateTime end = dateValidator.toTime(rangeEnd);
        dateValidator.validTime(start, end);
        Pageable page = PageRequest.of(from / size, size);
        BooleanBuilder query = new BooleanBuilder()
                .and(!CollectionUtils.isEmpty(users) ? QEvent.event.initiator.id.in(users) : null)
                .and(!CollectionUtils.isEmpty(categories) ? QEvent.event.category.id.in(categories) : null)
                .and(start != null ? QEvent.event.eventDate.goe(start) : null)
                .and(end != null ? QEvent.event.eventDate.loe(end) : null);

        if (!CollectionUtils.isEmpty(states)) {
            List<State> stateList = states.stream()
                    .map(State::valueOf)
                    .collect(Collectors.toList());
            query.and(QEvent.event.state.in(stateList));
        }
        Page<Event> events = eventRepository.findAll(query, page);
        return eventUtilService.listEventFull(events.toList());
    }

    @Override
    public EventFullDto updateFromAdminEvent(long eventId, UpdateEventAdminRequest eventDto) {
        Event event = validatorService.existEventById(eventId);
        Category category = validatorService.existCategoryById(eventDto.getCategory());
        dateValidator.validStartForUpdate(event.getEventDate());
        if (event.getState() != null) {
            eventValidator.validStateForUpdate(eventDto, event);
        }
        Location location = null;
        if (eventDto.getLocation() != null) {
            locationValidator.validLocation(eventDto.getLocation().getLat(), eventDto.getLocation().getLon());
            location = locationRepository.save(LocationMapper.toLocation(eventDto.getLocation()));
        }
        Event eventFromRepository = eventRepository.save(EventMapper.toEventFromAdminUpdateDto(event, eventDto,
                category, location));
        return eventUtilService.listEventFull(List.of(eventFromRepository)).get(0);
    }
}
