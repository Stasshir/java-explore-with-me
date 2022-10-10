package ru.practicum.ewm.admin;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional(propagation = Propagation.REQUIRED)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminServiceImplTest {

    private final AdminService adminService;
    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final CategoryRepository categoryRepository;
    @MockBean
    private final EventRepository eventRepository;

    @MockBean
    private final CompilationRepository compilationRepository;

    User user1 = User.builder().id(1).name("user1").email("user1@mail.ru").build();
    User user2 = User.builder().id(2).name("user2").email("user2@mail.ru").build();
    User userFailed = User.builder().name("").email("").build();
    UserDto userDto1 = UserDto.builder().name("user1").email("user1@mail.ru").build();
    UserDto userDtoFailed = UserDto.builder().name("").email("").build();
    Integer[] ids = {1, 2};
    Category category = Category.builder().id(1).name("category").build();
    CategoryDto categoryDto = CategoryDto.builder().id(1).name("categoryDto").build();
    Event event = Event.builder().id(1).annotation("annotation")
            .eventDate(LocalDateTime.now().plusDays(1)).created(LocalDateTime.now().minusDays(1))
            .description("desc").title("title").state(State.PENDING).build();
    Compilation compilation = Compilation.builder().title("title")
            .pinned(true).events(new HashSet<>()).build();
    NewCompilationDto newCompilationDto = NewCompilationDto.builder()
            .title("titleDto").pinned(true).events(new ArrayList<>()).build();

    AdminUpdateEventRequest eventRequest = AdminUpdateEventRequest.builder().annotation("annotationUpdate")
            .eventDate(LocalDateTime.now().plusDays(1))
            .description("descUpdate").title("titleUpdate").build();

    @Test
    void addUser_ShouldBeOk() {
        when(userRepository.save(any())).thenReturn(user1);
        UserDto userSavedDto = adminService.addUser(userDto1);
        assertEquals(userSavedDto.getEmail(), userDto1.getEmail(), "Пользователи не совпадают");
        assertEquals(userSavedDto.getName(), userDto1.getName(), "Пользователи не совпадают");
    }

    @Test
    void getUsers_ShouldBeOk() {
        when(userRepository.findById(1)).thenReturn(Optional.ofNullable(user1));
        when(userRepository.findById(2)).thenReturn(Optional.ofNullable(user2));
        var result = adminService.getUsers(ids, 0, 10);
        assertEquals(result.size(), 2, "Количество пользователей не совпадает");
        assertEquals(result.get(0).getName(), user1.getName(), "Пользователи не совпадают");
        assertEquals(result.get(1).getName(), user2.getName(), "Пользователи не совпадают");
    }

    @Test
    void addCategory_ShouldBeOk() {
        when(categoryRepository.save(any())).thenReturn(category);
        CategoryDto savedCategoryDto = adminService.addCategory(categoryDto);
        assertEquals(savedCategoryDto.getName(), category.getName(), "Категории не совпадают");
    }

    @Test
    void updateCategory_ShouldBeOk() {
        when(categoryRepository.findById(any())).thenReturn(Optional.ofNullable(category));
        when(categoryRepository.save(any())).thenReturn(category);
        CategoryDto savedCategoryDto = adminService.updateCategory(categoryDto);
        assertEquals(savedCategoryDto.getName(), category.getName(), "Категории не совпадают");
    }

    @Test
    void publishEvent_ShouldBeOk() {
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any())).thenReturn(event);
        EventFullDto eventFullDto = adminService.publishEvent(1);
        Mockito.verify(eventRepository, Mockito.times(1))
                .save(event);
        assertEquals(eventFullDto.getState(), State.PUBLISHED, "События не совпадают");
    }

    @Test
    void rejectEvent_ShouldBeOk() {
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any())).thenReturn(event);
        EventFullDto eventFullDto = adminService.rejectEvent(1);
        Mockito.verify(eventRepository, Mockito.times(1))
                .save(event);
        assertEquals(eventFullDto.getState(), State.CANCELED, "События не совпадают");
    }

    @Test
    void updateEvent_ShouldBeOk() {
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any())).thenReturn(event);
        EventFullDto eventFullDto = adminService.updateEvent(1, eventRequest);
        Mockito.verify(eventRepository, Mockito.times(1))
                .save(event);
        assertEquals(eventFullDto.getDescription(), eventRequest.getDescription(), "События не совпадают");
    }

    @Test
    void addCompilation_ShouldBeOk() {
        when(compilationRepository.save(any())).thenReturn(compilation);
        CompilationDto savedCompilationDto = adminService.addCompilation(newCompilationDto);
        assertEquals(savedCompilationDto.getTitle(), compilation.getTitle(), "Подборки не совпадают");
    }

    @Test
    void addEventToCompilation_ShouldBeOk() {
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(compilationRepository.findById(any())).thenReturn(Optional.ofNullable(compilation));
        when(compilationRepository.save(any())).thenReturn(compilation);
        adminService.addEventToCompilation(1, 1);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .save(compilation);
    }

    @Test
    void deleteEventFromCompilation_ShouldBeOk() {
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(compilationRepository.findById(any())).thenReturn(Optional.ofNullable(compilation));
        when(compilationRepository.save(any())).thenReturn(compilation);
        adminService.deleteEventFromCompilation(1, 1);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .save(compilation);
    }

    @Test
    void fixCompilation_ShouldBeOk() {
        when(compilationRepository.findById(any())).thenReturn(Optional.ofNullable(compilation));
        when(compilationRepository.save(any())).thenReturn(compilation);
        adminService.fixCompilation(1);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .save(compilation);
        assertTrue(compilation.isPinned(), "Статусы не совпадают");
    }

    @Test
    void notFixCompilation_ShouldBeOk() {
        when(compilationRepository.findById(any())).thenReturn(Optional.ofNullable(compilation));
        when(compilationRepository.save(any())).thenReturn(compilation);
        adminService.notFixCompilation(1);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .save(compilation);
        assertFalse(compilation.isPinned(), "Статусы не совпадают");
    }

    @Test
    void deleteCompilation_ShouldBeOk() {
        when(compilationRepository.existsById(any())).thenReturn(true);
        adminService.deleteCompilation(1);
        Mockito.verify(compilationRepository, Mockito.times(1))
                .deleteById(1);
    }

}