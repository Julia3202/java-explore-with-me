package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    CategoryDto getCategory(Long id);

    List<CategoryDto> getListCategories(Integer from, Integer size);

    void delete(Long id);

}
