package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventUtilService;
import ru.practicum.utils.CompilationValidator;
import ru.practicum.utils.ValidatorService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventUtilService utilService;
    private final ValidatorService validatorService;
    private final CompilationValidator compilationValidator = new CompilationValidator();

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        compilationValidator.validateTitle(newCompilationDto);
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        List<Event> eventList;
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        if (!newCompilationDto.getEventIdList().isEmpty()) {
            eventList = eventRepository.findAllById(newCompilationDto.getEventIdList());
            compilation.setEvents(eventList);
            eventShortDtoList = utilService.listEventShort(eventList);
        }
        Compilation compilationDto = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilationDto, eventShortDtoList);
    }

    @Override
    public CompilationDto update(Long id, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = validatorService.existCompilationById(id);
        List<EventShortDto> eventShortDtoList;
        if (updateCompilationDto.getEventIdList().isEmpty()) {
            eventShortDtoList = utilService.listEventShort(new ArrayList<>(compilation.getEvents()));
        } else {
            List<Event> eventList = eventRepository.findAllById(updateCompilationDto.getEventIdList());
            compilation.setEvents(eventList);
            eventShortDtoList = utilService.listEventShort(eventList);
        }
        Compilation compilationFromRepository = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilationFromRepository, eventShortDtoList);
    }

    @Override
    public CompilationDto getCompilation(Long id) {
        Compilation compilation = validatorService.existCompilationById(id);
        List<Event> eventList = new ArrayList<>(compilation.getEvents());
        List<EventShortDto> eventShortDtoList = utilService.listEventShort(eventList);
        return CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
    }

    @Override
    public List<CompilationDto> getCompilationList(Boolean pinned, Integer from, Integer size) {
        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = 10;
        }
        validatorService.validSizeAndFrom(from, size);
        Pageable page = PageRequest.of(from / size, size);
        List<Compilation> compilationList;
        if (pinned == null) {
            compilationList = compilationRepository.findAll(page).toList();
        } else {
            compilationList = compilationRepository.findAllByPinned(pinned, page).toList();
        }
        Map<Compilation, List<Long>> compilationMap = new HashMap<>();
        List<Event> eventList = new ArrayList<>();
        for (Compilation compilation : compilationList) {
            compilationMap.put(compilation, compilation.getEvents().stream()
                    .map(Event::getId)
                    .collect(Collectors.toList()));
            eventList.addAll(compilation.getEvents());
        }
        List<EventShortDto> eventShortDtoList = utilService.listEventShort(eventList);
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilationMap.keySet()) {
            List<EventShortDto> eventShortDtos = new ArrayList<>();
            for (EventShortDto eventShortDto : eventShortDtoList) {
                if (compilationMap.get(compilation).contains(eventShortDto.getId())) {
                    eventShortDtos.add(eventShortDto);
                }
            }
            CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation, eventShortDtos);
            compilationDtoList.add(compilationDto);
        }
        return compilationDtoList.stream()
                .sorted(Comparator.comparingLong(CompilationDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Compilation compilation = validatorService.existCompilationById(id);
        compilationRepository.delete(compilation);
    }
}
