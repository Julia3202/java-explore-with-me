package ru.practicum.location.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.location.model.Location;

@UtilityClass
public class LocationMapper {
    public Location toLocation(LocationDto locationDto) {
        return new Location(
                null,
                locationDto.getLat(),
                locationDto.getLon()
        );
    }

    public LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }
}
