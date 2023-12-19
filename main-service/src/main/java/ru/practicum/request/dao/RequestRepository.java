package ru.practicum.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;
import ru.practicum.utils.Status;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long id);

    List<Request> findAllByEventId(Long eventId);

    Integer countAllByStatusAndEventId(Status status, Long id);

    List<Request> findAllByIdIn(List<Long> requestIds);

    Long countByEventIdAndStatus(Long eventId, Status status);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);
}
