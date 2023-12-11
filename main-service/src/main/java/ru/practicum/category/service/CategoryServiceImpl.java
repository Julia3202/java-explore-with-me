package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.validator.CategoryValidator;
import ru.practicum.validator.ValidatorService;
import ru.practicum.validator.ValidatorSizeAndFrom;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ValidatorService validatorService;
    private final CategoryValidator categoryValidator = new CategoryValidator();
    private final ValidatorSizeAndFrom validatorSizeAndFrom = new ValidatorSizeAndFrom();

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        categoryValidator.validName(newCategoryDto);
        validatorService.uniqueName(newCategoryDto);
        Category category = CategoryMapper.toCategory(newCategoryDto);
        categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = validatorService.existCategoryById(id);
        Category categoryFromDto = CategoryMapper.toCategory(categoryDto, category);
        categoryFromDto.setId(id);
        categoryRepository.save(categoryFromDto);
        return CategoryMapper.toCategoryDto(categoryFromDto);
    }

    @Override
    public CategoryDto getCategory(Long id) {
        Category category = validatorService.existCategoryById(id);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getListCategories(Integer from, Integer size) {
        validatorSizeAndFrom.validFrom(from);
        validatorSizeAndFrom.validSize(size);
        Pageable page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Category category = validatorService.existCategoryById(id);
        categoryRepository.delete(category);
    }
}
