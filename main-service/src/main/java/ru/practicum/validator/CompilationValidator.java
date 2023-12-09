package ru.practicum.validator;

import org.apache.commons.lang3.StringUtils;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.exception.ValidationException;

public class CompilationValidator {
    public void validateTitle(NewCompilationDto newCompilationDto) {
        if (StringUtils.isBlank(newCompilationDto.getTitle())) {
            throw new ValidationException("Поле с названием обязательно к заполнению.");
        }
        if (newCompilationDto.getTitle().length() > 50) {
            throw new ValidationException("Поле с названием не должно быть длиннее 50 символов.");
        }
    }
}
