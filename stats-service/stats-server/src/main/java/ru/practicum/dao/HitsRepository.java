package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) from EndpointHit e " +
            "where e.timestamp between ?1 and ?2 group by e.app, e.uri order by count(e.ip) desc")
    List<ViewStatsDto> getUniqueStat(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) from EndpointHit as e " +
            "where e.timestamp >= ?1 and e.timestamp <= ?2 and e.uri in ?3 group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStatsDto> getUniqueStatWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ViewStatsDto(e.app, e.uri, count(e.ip)) from EndpointHit e " +
            "where e.timestamp >= ?1 and e.timestamp <= ?2 group by e.app, e.uri order by count(e.ip) desc")
    List<ViewStatsDto> getStat(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ViewStatsDto(e.app, e.uri, count(e.ip)) from EndpointHit e " +
            "where e.timestamp >= ?1 and e.timestamp <= ?2 and e.uri in ?3 group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStatsDto> getStatWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
