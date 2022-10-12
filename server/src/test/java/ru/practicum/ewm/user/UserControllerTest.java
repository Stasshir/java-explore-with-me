package ru.practicum.ewm.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.participiation.dto.ParticipationRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    EventFullDto eventFullDto = EventFullDto.builder().id(1).description("desc")
            .build();
    EventShortDto eventShortDto = EventShortDto.builder().id(2).annotation("annotation").build();

    ParticipationRequestDto participationRequestDto = ParticipationRequestDto.builder().id(1).event(1).build();

    @Test
    void addEvents() throws Exception {
        when(userService.addEvents(any(), anyInt()))
                .thenReturn(eventFullDto);
        mockMvc.perform(post("/users/1/events")
                        .content(objectMapper.writeValueAsString(eventFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription())));
    }

    @Test
    void getEventsByUserId() throws Exception {
        when(userService.getEventsByUserId(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(eventShortDto));
        mockMvc.perform(get("/users/1/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$..annotation", is(List.of(eventShortDto.getAnnotation()))));
    }

    @Test
    void updateEvent() throws Exception {
        when(userService.updateEvent(any(), anyInt()))
                .thenReturn(eventFullDto);
        mockMvc.perform(patch("/users/1/events")
                        .content(objectMapper.writeValueAsString(eventFullDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription())));
    }

    @Test
    void confirmRequests() throws Exception {
        when(userService.confirmRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(participationRequestDto);
        mockMvc.perform(patch("/users/1/events/1/requests/1/confirm")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(participationRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.event", is(participationRequestDto.getEvent())));
    }
}