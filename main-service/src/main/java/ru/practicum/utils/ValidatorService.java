package ru.practicum.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
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

    public User existUserById(Long userId) {
        log.info("user");
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID- " + userId + " не найден."));
    }

    public Event existEventById(Long id) {
        log.info("exist event");
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

    public void uniqueName(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findByName(newCategoryDto.getName());
        if (category != null) {
            throw new ConflictException("Категория с именем-" + newCategoryDto.getName() + " уже создана.");
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
