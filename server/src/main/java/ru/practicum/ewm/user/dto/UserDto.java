package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
@AllArgsConstructor
public class UserDto {

    private int id;
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @Email(message = "Неправильно введена почта")
    private String email;
}
