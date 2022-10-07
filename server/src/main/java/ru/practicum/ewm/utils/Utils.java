package ru.practicum.ewm.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.events.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Utils {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public Category map(int value) {
        return categoryRepository.findById(value).orElseThrow();
    }

    public Set<Event> map(List<Integer> value) {
        Set<Event> events = new HashSet<>();
        value.forEach(i -> events.add(eventRepository.findById(i).orElseThrow()));
        return events;
    }

    public String map(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }
}
