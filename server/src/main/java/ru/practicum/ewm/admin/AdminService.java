package ru.practicum.ewm.admin;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface AdminService {

    UserDto addUser(UserDto userDto);

    void deleteUser(int id);

    List<UserDto> getUsers(Integer[] ids, int from, int size);

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(int id);

    EventFullDto publishEvent(int eventId);

    EventFullDto rejectEvent(int eventId);

    EventFullDto updateEvent(int eventId, AdminUpdateEventRequest eventRequest);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void addEventToCompilation(int compId, int eventId);

    void deleteEventFromCompilation(int compId, int eventId);

    void fixCompilation(@PathVariable int compId);

    void notFixCompilation(@PathVariable int compId);

    void deleteCompilation(int compId);

    List<EventFullDto> findEvents(int[] users, String[] states, int[] categories, String rangeStart,
                                  String rangeEnd, int from, int size);
}
