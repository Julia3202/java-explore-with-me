package ru.practicum.user.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByIdIn(Pageable page, Set<Long> ids);

    User findByEmail(String email);
}
