package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class NewCompilationDto {
    @NotBlank(message = "Наименование подборки обязательно для заполнения")
    @Size(min = 1, max = 50, message = "Наименование подборки содержать не менее 1 и не более 50 симоволов")
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
