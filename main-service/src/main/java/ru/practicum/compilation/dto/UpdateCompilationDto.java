package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCompilationDto {
    private String title;
    private Boolean pinned;
    private List<Long> eventIdList;
}
