package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ValidationException;
import ru.practicum.utils.ValidatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.compilation.dto.CompilationMapper.COMPILATION_MAPPER;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ValidatorService validatorService;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        validateTitle(newCompilationDto.getTitle());
        List<Event> events = !CollectionUtils.isEmpty(newCompilationDto.getEvents()) ?
                eventRepository.findAllById(newCompilationDto.getEvents()) : new ArrayList<>();
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilationDto = compilationRepository.save(COMPILATION_MAPPER.toModel(newCompilationDto, events));
        return COMPILATION_MAPPER.toCompilationDto(compilationDto);
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = validatorService.existCompilationById(compId);
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> eventList = eventRepository.findAllById(updateCompilationRequest.getEvents());
            compilation.setEvents(eventList);
        }
        Optional.ofNullable(updateCompilationRequest.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateCompilationRequest.getPinned()).ifPresent(compilation::setPinned);
        Compilation compilationFromRepository = compilationRepository.save(compilation);
        return COMPILATION_MAPPER.toCompilationDto(compilationFromRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long id) {
        Compilation compilation = validatorService.existCompilationById(id);
        return COMPILATION_MAPPER.toCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilationList(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return compilationRepository.findAllByPinnedIsNullOrPinned(pinned, page).stream()
                .map(COMPILATION_MAPPER::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Compilation compilation = validatorService.existCompilationById(id);
        compilationRepository.delete(compilation);
    }

    public static void validateTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new ValidationException("Поле с названием обязательно к заполнению.");
        }
        if (title.length() > 50) {
            throw new ValidationException("Поле с названием не должно быть длиннее 50 символов.");
        }
    }
}
