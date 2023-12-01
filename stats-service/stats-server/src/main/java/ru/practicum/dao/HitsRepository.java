package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitsRepository extends JpaRepository<EndpointHit, Long> {

    List<ViewStatsDto> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<ViewStatsDto> findDistinctByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<ViewStatsDto> findAllByTimestampBetweenAndUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStatsDto> findDistinctByTimestampBetweenAndUri(LocalDateTime start, LocalDateTime end, List<String> uris);
}
