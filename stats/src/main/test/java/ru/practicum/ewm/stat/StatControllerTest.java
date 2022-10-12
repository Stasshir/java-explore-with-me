package ru.practicum.ewm.stat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.stat.dto.ViewStats;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatController.class)
class StatControllerTest {


    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StatService statService;

    @Autowired
    private MockMvc mockMvc;

    ViewStats viewStats = ViewStats.builder().hits(1).app("app").build();

    @Test
    void getStat() throws Exception {
        when(statService.getStat(anyString(), anyString(), any(), anyBoolean()))
                .thenReturn(Set.of(viewStats));
        mockMvc.perform(get("/stats?start=10.10.2000&end=10.10.2030&uris=1&unique=false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$..app", is(List.of(viewStats.getApp()))));
    }
}