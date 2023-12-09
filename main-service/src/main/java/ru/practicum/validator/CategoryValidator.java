package ru.practicum.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.exception.ValidationException;

@Slf4j
public class CategoryValidator {
    public boolean validName(NewCategoryDto newCategoryDto) {
        if (StringUtils.isBlank(newCategoryDto.getName())) {
            log.info("Поле с именем должно быть заполнено.");
            throw new ValidationException("Заполните поле с именем.");
        }
        return true;
    }
}
