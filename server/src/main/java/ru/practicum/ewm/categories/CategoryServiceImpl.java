package ru.practicum.ewm.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryMaper;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMaper categoryMaper;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.stream().map(categoryMaper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoriesById(int catId) {
        return categoryMaper.toCategoryDto(categoryRepository
                .findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена")));
    }
}
