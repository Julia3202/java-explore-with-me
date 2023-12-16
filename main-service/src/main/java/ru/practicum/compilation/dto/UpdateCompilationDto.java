package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class UpdateCompilationDto {
    @Size(min = 1, max = 50, message = "Наименование подборки содержать не менее 1 и не более 50 симоволов")
    private String title;
    private Boolean pinned;
    private List<Long> eventIdList;
}
