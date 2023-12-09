package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class NewCompilationDto {
    private String title;
    private Boolean pinned;
    private List<Long> eventIdList;
}
