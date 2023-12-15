package ru.practicum.compilation.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("select c " +
            "from Compilation c " +
            "where ((:pinned) is null or c.pinned = :pinned)")
    Page<Compilation> findAllByPinned(Boolean pinned, Pageable page);

    @Query("select c " +
            "from Compilation c " +
            "where ((:pinned) is null or c.pinned = :pinned)")
    List<Compilation> findAllByPinnedIsNullOrPinned(Boolean pinned, Pageable page);
}
