package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.utils.CompilationValidator;
import ru.practicum.utils.ValidatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.compilation.dto.CompilationMapper.COMPILATION_MAPPER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ValidatorService validatorService;
    private final CompilationValidator compilationValidator = new CompilationValidator();

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        compilationValidator.validateTitle(newCompilationDto);
        List<Event> events = newCompilationDto.getEvents() != null &&
                !newCompilationDto.getEvents().isEmpty() ?
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
        log.info("!!!!!!!!!!!!!Compilation check");
        log.info("Compilation event = {}", updateCompilationRequest.getEventIdList());
        if (updateCompilationRequest.getEventIdList() != null) {
            List<Event> eventList = eventRepository.findAllById(updateCompilationRequest.getEventIdList());
            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!size eventList = {}", eventList.size());

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
}
