package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class NewCompilationDto {
    @NotBlank(message = "Наименование подборки обязательно для заполнения")
    @Size(min = 1, max = 50, message = "Наименование подборки содержать не менее 1 и не более 50 симоволов")
    private String title;
    private Boolean pinned;
    private List<Long> eventIdList;
}
