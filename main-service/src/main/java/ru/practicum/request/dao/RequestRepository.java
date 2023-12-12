package ru.practicum.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long id);

    List<Request> findAllByStatusAndEventIdIn(Status status, List<Long> eventsIds);

    List<Request> findAllByEventId(Long eventId);

    Integer countAllByStatusAndEventId(Status status, Long id);

    List<Request> findAllByIdIn(List<Long> requestIds);

    List<Request> findAllByEventIdAndStatus(Long id, Status status);

    @Query("update Request as r set r.status = ?1 where r.id in ?2")
    void requestStatusUpdate(Status status, List<Long> requestsIdsForConfirm);

    Optional<Request> findAllByRequesterIdAndEventId(Long userId, Long eventId);
}
