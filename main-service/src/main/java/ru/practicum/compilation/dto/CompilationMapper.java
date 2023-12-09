package ru.practicum.compilation.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilationDto){
        return new Compilation(
                null,
                newCompilationDto.getTitle(),
                newCompilationDto.getPinned(),
                new ArrayList<>()
        );
    }

    public CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtos){
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                eventShortDtos
        );
    }

    public Compilation toCompilation(UpdateCompilationDto updateCompilationDto, Compilation compilation){
        return new Compilation(
                compilation.getId(),
                updateCompilationDto.getTitle() != null ? updateCompilationDto.getTitle() : compilation.getTitle(),
                updateCompilationDto.getPinned() != null ? updateCompilationDto.getPinned() : compilation.getPinned(),
                updateCompilationDto.getEventIdList() != null ? new ArrayList<>() : compilation.getEvents()

        );
    }
}
