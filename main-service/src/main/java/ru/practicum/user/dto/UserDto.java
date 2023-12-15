package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Error! Email can't be blank.")
    @Size(max = 254, min = 6, message = "Error! Email length must be between 6 and 254 characters.")
    @Email(message = "Error! Wrong email.")
    private String name;

    @NotBlank(message = "Error! Email can't be blank.")
    @Size(max = 254, min = 6, message = "Error! Email length must be between 6 and 254 characters.")
    @Email(message = "Error! Wrong email.")
    private String email;
}
