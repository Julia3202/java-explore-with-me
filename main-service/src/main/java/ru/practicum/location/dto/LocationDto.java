package ru.practicum.location.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LocationDto {
    private Double lat;
    private Double lon;
}
