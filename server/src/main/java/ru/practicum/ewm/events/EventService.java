package ru.practicum.ewm.events;

import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface EventService {

    List<EventShortDto> getEvents(String text, int[] categories, boolean paid, String rangeStart, String rangeEnd,
                                  boolean onlyAvailable, String sort, int from, int size,
                                  HttpServletRequest request);

    EventFullDto getEventById(int id, HttpServletRequest request);
}
