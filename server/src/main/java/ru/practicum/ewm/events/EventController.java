package ru.practicum.ewm.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @GetMapping
    List<EventShortDto> getEvents(@RequestParam @NotNull String text,
                                  @RequestParam @NotNull int[] categories,
                                  @RequestParam boolean paid,
                                  @RequestParam(defaultValue = "") String rangeStart,
                                  @RequestParam(defaultValue = "") String rangeEnd,
                                  @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                  @RequestParam String sort,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(defaultValue = "10") @Positive int size,
                                  HttpServletRequest request) {

        log.info("Запрос на поиск событий получен, catId={}, text={}", categories, text);
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, request);
    }

    @GetMapping("/{id}")
    EventFullDto getEventsById(@PathVariable int id, HttpServletRequest request) {
        log.info("Запрос на поиск событий по ID получен, Id={}", id);
        return eventService.getEventById(id, request);
    }

}
