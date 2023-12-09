package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(Long id, UpdateCompilationDto updateCompilationDto);

    CompilationDto getCompilation(Long id);

    List<CompilationDto> getCompilationList(Boolean pinned, Integer from, Integer size);

    void delete(Long id);
}
