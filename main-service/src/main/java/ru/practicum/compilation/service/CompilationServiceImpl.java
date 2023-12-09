package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.validator.CompilationValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationValidator compilationValidator = new CompilationValidator();

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        compilationValidator.validateTitle(newCompilationDto);
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        List<Event> eventList = new ArrayList<>();
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        if(!newCompilationDto.getEventIdList().isEmpty()){
            eventList = eventRepository.findAllById(newCompilationDto.getEventIdList());
            compilation.setEvents(eventList);
            for(Event events: eventList){
                eventShortDtoList.add(EventMapper.toEventShortDto(events, 1, 2L));
            }
        }
        Compilation compilationDto = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilationDto, eventShortDtoList);
    }

    @Override
    public CompilationDto update(Long id, UpdateCompilationDto updateCompilationDto) {
        return null;
    }

    @Override
    public CompilationDto getCompilation(Long id) {
        return null;
    }

    @Override
    public List<CompilationDto> getCompilationList(Boolean pinned, Integer from, Integer size) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
