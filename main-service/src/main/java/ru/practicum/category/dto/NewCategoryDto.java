package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Имя категории обязательно для заполнения")
    @Size(min = 1, max = 50, message = "Имя должно содержать не менее 1 и не более 50 симоволов")
    private String name;
}
