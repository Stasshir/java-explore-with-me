package ru.practicum.ewm.categories;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional(propagation = Propagation.REQUIRED)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryServiceImplTest {

    private final CategoryService categoryService;
    @MockBean
    private final CategoryRepository categoryRepository;

    Category category = Category.builder().id(1).name("category").build();

    @Test
    void getCategories_shouldBeOk() {
        when(categoryRepository.findAll((Pageable) any())).thenReturn(new PageImpl<>(List.of(category)));
        var result = categoryService.getCategories(0, 10);
        assertEquals(result.size(), 1, "Количество категорий не совпадает");
        assertEquals(result.get(0).getName(), category.getName(), "Наименование категорий совпадает");
    }
}