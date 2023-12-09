package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class NewUserDto {
    @Email(message = "Неверный формат электронной почты.")
    private String email;

    private String name;
}
