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
    @Size(min = 6, max = 254, message = "Email должн содержать не менее 6 и не более 254 симоволов")
    private String email;

    @NotBlank(message = "Имя пользователя обязательно для заполнения")
    @Size(min = 2, max = 250, message = "Имя должно содержать не менее 2 и не более 250 симоволов")
    private String name;
}
