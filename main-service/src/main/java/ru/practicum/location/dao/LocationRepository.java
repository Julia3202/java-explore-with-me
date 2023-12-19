package ru.practicum.location.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
