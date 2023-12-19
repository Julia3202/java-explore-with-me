package ru.practicum.event.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.location.model.Location;

@Mapper
public interface EventMapper {
    EventMapper EVENT_MAPPER = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    Event fromDto(NewEventDto newEventDto, Category category, Location location);

    EventFullDto toFullDto(Event event);

    EventShortDto toShortDto(Event event);
}
