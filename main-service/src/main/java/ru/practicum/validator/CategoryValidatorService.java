package ru.practicum.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

@RequiredArgsConstructor
@Service
public class CategoryValidatorService {
    private final CategoryRepository categoryRepository;

    public Category existCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с ID-" + id + " не найдена."));
        return category;
    }

    public void uniqueName(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findByName(newCategoryDto.getName());
        if (category != null) {
            throw new ValidationException("Категория с именем-" + newCategoryDto.getName() + " уже создана.");
        }
    }
}
