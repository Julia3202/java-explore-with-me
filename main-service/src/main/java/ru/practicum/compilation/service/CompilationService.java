package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(Long id, UpdateCompilationRequest updateCompilationRequest);

    CompilationDto getCompilation(Long id);

    List<CompilationDto> getCompilationList(Boolean pinned, Integer from, Integer size);

    void delete(Long id);
}
