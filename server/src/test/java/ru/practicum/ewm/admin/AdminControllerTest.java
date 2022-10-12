package ru.practicum.ewm.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
class AdminControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AdminService adminService;

    @Autowired
    private MockMvc mockMvc;

    UserDto userDto = UserDto.builder().id(1).email("user1@email.ru").name("user1").build();
    CategoryDto categoryDto = CategoryDto.builder().id(1).name("cat").build();
    EventFullDto eventFullDto = EventFullDto.builder().id(1).description("desc")
            .eventDate(LocalDateTime.now().toString()).build();
    CompilationDto compilationDto = CompilationDto.builder().id(1).title("titlle").build();

    @Test
    void addNewUser() throws Exception {
        when(adminService.addUser(any()))
                .thenReturn(userDto);
        mockMvc.perform(post("/admin/users/")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Test
    void getUser() throws Exception {
        when(adminService.getUsers(any(), anyInt(), anyInt()))
                .thenReturn(List.of(userDto));
        mockMvc.perform(get("/admin/users?ids=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$..name", is(List.of(userDto.getName()))));
    }

    @Test
    void addCategories() throws Exception {
        when(adminService.addCategory(any()))
                .thenReturn(categoryDto);
        mockMvc.perform(post("/admin/categories/")
                        .content(objectMapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    void updateCategories() throws Exception {
        when(adminService.updateCategory(any()))
                .thenReturn(categoryDto);
        mockMvc.perform(patch("/admin/categories/")
                        .content(objectMapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    void publishEvent() throws Exception {
        when(adminService.publishEvent(anyInt()))
                .thenReturn(eventFullDto);
        mockMvc.perform(patch("/admin/events/1/publish")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription())));
    }

    @Test
    void rejectEvent() throws Exception {
        when(adminService.rejectEvent(anyInt()))
                .thenReturn(eventFullDto);
        mockMvc.perform(patch("/admin/events/1/reject")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription())));
    }

    @Test
    void addCompilation() throws Exception {
        when(adminService.addCompilation(any()))
                .thenReturn(compilationDto);
        mockMvc.perform(post("/admin/compilations")
                        .content(objectMapper.writeValueAsString(compilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(compilationDto.getId()), Integer.class))
                .andExpect(jsonPath("$.title", is(compilationDto.getTitle())));
    }
}