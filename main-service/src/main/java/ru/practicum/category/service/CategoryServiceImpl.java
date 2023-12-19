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
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.utils.ValidatorService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ValidatorService validatorService;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        validatorService.isUniqueName(newCategoryDto.getName());
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
        Pageable page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Category category = validatorService.existCategoryById(id);
        List<Event> eventList = eventRepository.findAllByCategoryId(id);
        if (!eventList.isEmpty()) {
            throw new ConflictException("Удаление невозможно, к категории относится " + eventList.size() + " событий.");
        }
        categoryRepository.delete(category);
    }
}
