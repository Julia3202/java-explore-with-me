package ru.practicum.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query("select min(e.publishedOn) from Event as e where e.id in ?1")
    Optional<LocalDateTime> getMinPublishedDate(List<Long> eventIdList);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    List<Event> findAllByCategoryId(Long catId);

    @Query("select e " +
            "from Event e " +
            "where ((:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (e.eventDate between :rangeStart and :rangeEnd))")
    List<Event> findAllByAdmin(@Param("users") List<Long> users, @Param("states") List<String> states,
                               @Param("categories") List<Long> categories, @Param("rangeStart") String rangeStart,
                               @Param("rangeEnd") String rangeEnd, Pageable page);
}
