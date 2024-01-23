package ru.practicum.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.comment.dao.CommentRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.model.Request;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

@RequiredArgsConstructor
@Service
@Slf4j
public class ValidatorService {
    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;
    private final UserRepository userRepository;
    public final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    public User existUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID- " + userId + " не найден."));
    }

    public Event existEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с ID- " + id + " не найдено."));
    }

    public Category existCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с ID-" + id + " не найдена."));
    }

    public Compilation existCompilationById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с ID-" + id + " не найдена."));
    }

    public Request existRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос с ID-" + id + " не найден."));
    }

    public Comment existCommentById(Long comId) {
        return commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментарий с ID- " + comId + " не найден."));
    }

    public void isUniqueName(String name) {
        Category category = categoryRepository.findByName(name);
        if (category != null) {
            throw new ConflictException("Категория с именем-" + name + " уже создана.");
        }
    }

    public void validSizeAndFrom(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Значение первого элемента должно быть строго больше 0.");
        }
        if (size <= 0) {
            throw new ValidationException("Количество выводимых строк строго должно быть больше 0.");
        }
    }
}
