package ru.practicum.event.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
public class NewEventDto {
    @NotBlank(message = "Краткое описание события обязательно для заполнения")
    @Size(min = 20, max = 2000, message = "Краткое описание должно содержать не менее 20 и не более 2000 симоволов")
    private String annotation;

    @NotNull
    @Positive(message = "Id category не может быть отрицательным или ноль")
    private Long category;

    @NotBlank(message = "Полное описание события обязательно для заполнения")
    @Size(min = 20, max = 7000, message = "Полное описание описание должно содержать не менее 20 и не более 7000 симоволов")
    private String description;

    @NotNull
    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "participantLimit не может быть отрицательным")
    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Заголовок должн содержать не менее 3 и не более 120 симоволов")
    @NotBlank
    private String title;
}
