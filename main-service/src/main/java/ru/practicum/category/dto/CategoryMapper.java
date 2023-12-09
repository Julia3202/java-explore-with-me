package ru.practicum.category.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.category.model.Category;

@UtilityClass
public class CategoryMapper {
    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public Category toCategory(NewCategoryDto newCategoryDto) {
        return new Category(
                null,
                newCategoryDto.getName()
        );
    }

    public Category toCategory(CategoryDto categoryDto, Category category) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName() != null ? categoryDto.getName() : category.getName()
        );
    }
}
