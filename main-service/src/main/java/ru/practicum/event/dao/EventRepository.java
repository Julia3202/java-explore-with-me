package ru.practicum.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.utils.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query("select e " +
            "from Event e " +
            "where (e.state = 'PUBLISHED') " +
            "and (lower(e.annotation) like lower(concat('%', :text, '%')) " +
            "or lower(e.description) like lower(concat('%', :text, '%'))) " +
            "and ((:categories) is null or e.category.id in :categories) " +
            "and ((:paid) is null or e.paid = :paid) " +
            "and (e.eventDate between :rangeStart and :rangeEnd)")
    List<Event> findAllByPublic(@Param("text") String text, @Param("paid") Boolean paid,
                                @Param("categories") List<Long> categories,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd);

    @Query("select e " +
            "from Event e " +
            "where ((:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (e.eventDate between :rangeStart and :rangeEnd))")
    List<Event> findAllByAdmin(@Param("users") List<Long> users, @Param("states") List<State> states,
                               @Param("categories") List<Long> categories, @Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    List<Event> findAllByCategoryId(Long id);
}
