package ru.practicum.ewm.categories.dto;

import org.mapstruct.Mapper;
import ru.practicum.ewm.categories.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMaper {

    CategoryDto toCategoryDto(Category category);

    Category toCategory(CategoryDto categoryDto);

    List<CategoryDto> toCategoryDto(List<Category> categoryList);

}
