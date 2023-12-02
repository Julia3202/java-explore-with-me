package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.dao.HitsRepository;
import ru.practicum.dto.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.TimeValidator;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.model.Constants.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final HitsRepository hitsRepository;
    private final TimeValidator timeValidator = new TimeValidator();

    @Override
    public EndpointHitDto createHits(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = hitsRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
        return EndpointHitMapper.toEndpointHitDto(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(String startTime, String endTime, List<String> uris, boolean unique) {
        LocalDateTime start = LocalDateTime.parse(startTime, DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endTime, DATE_TIME_FORMATTER);
        timeValidator.validTime(start, end);
        List<ViewStatsDto> veiwStatsDtoList = unique ?
                CollectionUtils.isEmpty(uris) ?
                        hitsRepository.getUniqueStat(start, end) :
                        hitsRepository.getUniqueStatWithUris(start, end, uris) :
                CollectionUtils.isEmpty(uris) ?
                        hitsRepository.getStat(start, end) :
                        hitsRepository.getStatWithUris(start, end, uris);
        return veiwStatsDtoList;
    }
}
