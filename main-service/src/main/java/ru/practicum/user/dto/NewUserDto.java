package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class NewUserDto {

    @NotBlank(message = "Почта пользователя обязательна для заполнения")
    @Email(message = "Передан неправильный формат email")
    @Size(max = 254, min = 6, message = "Error! Email length must be between 6 and 254 characters.")
    private String email;

    @NotBlank(message = "Имя пользователя обязательно для заполнения")
    @Size(max = 250, min = 2, message = "Error! Name length must be between 2 and 250 characters.")
    private String name;
}
