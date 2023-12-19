package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("Поступил запрос на получении категории с ID - {}.", catId);
        return categoryService.getCategory(catId);
    }

    @GetMapping
    public List<CategoryDto> getListCategory(@RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос на получение всех категорий (переданные данные): from = {}, size = {}", from, size);
        return categoryService.getListCategories(from, size);
    }
}
