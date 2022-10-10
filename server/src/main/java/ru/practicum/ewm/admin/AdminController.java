package ru.practicum.ewm.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users")
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя получен");
        return adminService.addUser(userDto);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Запрос на удаление пользователя получен, ID={}", userId);
        adminService.deleteUser(userId);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(value = "ids") @NotNull Integer[] ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос на вывод пользователей получен, ID={}", ids);
        return adminService.getUsers(ids, from, size);
    }

    @PostMapping("/categories")
    public CategoryDto addCategories(@RequestBody CategoryDto categoryDto) {
        log.info("Запрос на добавление новой категории получен");
        return adminService.addCategory(categoryDto);
    }

    @PatchMapping("/categories")
    public CategoryDto updateCategories(@RequestBody CategoryDto categoryDto) {
        log.info("Запрос на обновление категории получен");
        return adminService.updateCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategories(@PathVariable int catId) {
        log.info("Запрос на удаление категории получен, ID={}", catId);
        adminService.deleteCategory(catId);
    }


    //Публикация события
    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable int eventId) {
        log.info("Запрос на публикацию события получен, ID={}", eventId);
        return adminService.publishEvent(eventId);
    }

    //Отклонение события
    @PatchMapping("/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable int eventId) {
        log.info("Запрос на отклонение события получен, ID={}", eventId);
        return adminService.rejectEvent(eventId);
    }


    //Редактирование события
    @PutMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable int eventId,
                                    @RequestBody AdminUpdateEventRequest eventRequest) {
        log.info("Запрос на обновление события получен, ID={}", eventId);
        return adminService.updateEvent(eventId, eventRequest);
    }

    //Поиск событий
    @GetMapping("/events")
    public List<EventFullDto> findEvents(@RequestParam int[] users,
                                         @RequestParam String[] states,
                                         @RequestParam int[] categories,
                                         @RequestParam String rangeStart,
                                         @RequestParam String rangeEnd,
                                         @RequestParam @PositiveOrZero int from,
                                         @RequestParam @Positive int size) {
        log.info("Запрос на поиск событий получен");
        return adminService.findEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    // Добавление новой подборки
    @PostMapping("/compilations")
    public CompilationDto addCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        log.info("Запрос на добавление подборки событий получен");
        return adminService.addCompilation(newCompilationDto);
    }

    //Удаление подборки
    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable int compId) {
        log.info("Запрос на удаление подборки событий получен, ID={}", compId);
        adminService.deleteCompilation(compId);
    }


    //Добавить событие в подборку
    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable int compId, @PathVariable int eventId) {
        log.info("Запрос на добавление события в подборку получен, EventI={}, CompId={}", eventId, compId);
        adminService.addEventToCompilation(compId, eventId);
    }

    //Удалить событие из подборки
    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable int compId, @PathVariable int eventId) {
        log.info("Запрос на удаление события из подборки получен, EventI={}, CompId={}", eventId, compId);
        adminService.deleteEventFromCompilation(compId, eventId);
    }


    //Закрепить подборку на главной странице
    @PatchMapping("/compilations/{compId}/pin")
    public void fixCompilation(@PathVariable int compId) {
        log.info("Запрос на закрепление на главном эркане подборки событий получен, Id={}", compId);
        adminService.fixCompilation(compId);
    }


    //Удалить подборку с главной страницы
    @DeleteMapping("/compilations/{compId}/pin")
    public void notFixCompilation(@PathVariable int compId) {
        log.info("Запрос на открепление на главном эркане подборки событий получен, Id={}", compId);
        adminService.notFixCompilation(compId);
    }
}
