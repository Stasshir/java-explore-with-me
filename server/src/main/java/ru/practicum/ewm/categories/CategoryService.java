package ru.practicum.ewm.categories;

import ru.practicum.ewm.categories.dto.CategoryDto;

import java.util.List;


public interface CategoryService {

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoriesById(int catId);
}
