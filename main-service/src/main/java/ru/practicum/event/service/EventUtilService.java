package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatClient;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventUtilService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;

    public List<EventShortDto> listEventShort(List<Event> eventList) {
        List<Long> eventIdList = eventList.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Optional<LocalDateTime> start = eventRepository.getMinPublishedDate(eventIdList);

        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        if (start.isPresent()) {
            Map<Long, Long> veiws = getStatsForEvents(start.get(), eventIdList);
            Map<Long, Integer> eventsRequests = getEventRequests(Status.CONFIRMED, eventIdList);
            for (Event event : eventList) {
                eventShortDtoList.add(
                        EventMapper.toEventShortDto(event, eventsRequests.getOrDefault(event.getId(), 0),
                                veiws.getOrDefault(event.getId(), 0L))
                );
            }
        } else {
            for (Event event : eventList) {
                eventShortDtoList.add(EventMapper.toEventShortDto(event, 0, 0L));
            }
        }

        return eventShortDtoList;
    }

    public List<EventFullDto> listEventFull(List<Event> eventList) {
        List<Long> eventsIds = eventList.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Optional<LocalDateTime> times = eventRepository.getMinPublishedDate(eventsIds);
        List<EventFullDto> eventShortDtoList = new ArrayList<>();
        if (times.isPresent()) {
            Map<Long, Long> veiws = getStatsForEvents(times.get(), eventsIds);
            Map<Long, Integer> eventsRequests = getEventRequests(Status.CONFIRMED, eventsIds);
            for (Event event : eventList) {
                eventShortDtoList.add(
                        EventMapper.toEventFullDto(event, eventsRequests.getOrDefault(event.getId(), 0),
                                veiws.getOrDefault(event.getId(), 0L))
                );
            }
        } else {
            for (Event event : eventList) {
                eventShortDtoList.add(EventMapper.toEventFullDto(event, 0, 0L));
            }
        }

        return eventShortDtoList;
    }

    private Map<Long, Long> getStatsForEvents(LocalDateTime times, List<Long> eventsIds) {
        List<String> uries = eventsIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        List<ViewStatsDto> veiwStatsDtoList = statClient.getStats(times, LocalDateTime.now(), uries, true);
        Map<Long, Long> veiws = new HashMap<>();
        for (ViewStatsDto veiwStatsDto : veiwStatsDtoList) {
            String uri = veiwStatsDto.getUri();
            Long eventId = Long.parseLong(uri.substring(uri.lastIndexOf("/") + 1));
            veiws.put(eventId, veiwStatsDto.getHits());
        }
        return veiws;
    }

    private Map<Long, Integer> getEventRequests(Status status, List<Long> eventsIds) {
        List<Request> requestList = requestRepository.findAllByStatusAndEventIdIn(status, eventsIds);
        Map<Long, Integer> eventsRequests = new HashMap<>();
        for (Request request : requestList) {
            if (eventsRequests.containsKey(request.getId())) {
                Integer count = eventsRequests.get(request.getId());
                eventsRequests.put(request.getEvent().getId(), ++count);
            } else {
                eventsRequests.put(request.getEvent().getId(), 1);
            }
        }
        return eventsRequests;
    }
}
