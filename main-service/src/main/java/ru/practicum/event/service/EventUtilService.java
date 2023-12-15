package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatClient;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventUtilService {

    private final RequestRepository requestRepository;
    private final StatClient statClient;


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
