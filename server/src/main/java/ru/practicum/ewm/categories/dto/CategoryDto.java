package ru.practicum.ewm.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class CategoryDto {

    private int id;

    @NotBlank(message = "Наименование категории не может быть пустым")
    private String name;
}

