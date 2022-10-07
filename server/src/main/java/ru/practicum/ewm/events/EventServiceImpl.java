package ru.practicum.ewm.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.client.EventClient;
import ru.practicum.ewm.events.client.ViewStats;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventMaper;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMaper eventMaper;
    private final EventClient eventClient;
    private final LocalDateTime startDate = LocalDateTime.of(2000, 01, 01, 01, 01, 01);
    private final LocalDateTime endDate = LocalDateTime.of(2100, 01, 01, 01, 01, 01);


    @Override
    public List<EventShortDto> getEvents(String text, int[] categories, boolean paid, String rangeStart,
                                         String rangeEnd, boolean onlyAvailable, String sort,
                                         int from, int size, HttpServletRequest request) {
        eventClient.postStat("ewm-main-service", request.getRequestURI(), request.getRemoteAddr());
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart.equals("") && rangeEnd.equals("")) {
            start = LocalDateTime.now();
            end = LocalDateTime.MAX;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
        }
        Pageable p = PageRequest.of(from, size);
        text = "%" + text + "%";
        List<Event> events = eventRepository.getEvents(text, categories, paid, start, end, p).stream().collect(Collectors.toList());
        if (onlyAvailable)
            events = events.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());

        if (sort.equals("VIEWS"))
            events = events.stream().sorted(Comparator.comparingInt(Event::getViews))
                    .collect(Collectors.toList());
        else
            events = events.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .collect(Collectors.toList());
        return events.stream().map(eventMaper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(int id, HttpServletRequest request) {
        Event event = eventRepository.findEventByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        eventClient.postStat("ewm-main-service", request.getRequestURI(), request.getRemoteAddr());
        ResponseEntity<List<ViewStats>> resp = eventClient.getStats(startDate, endDate,
                request.getRequestURI());
        List<ViewStats> viewStats = resp.getBody();
        int views = viewStats.stream()
                .filter(viewStats1 -> viewStats1.getUri().equals(request.getRequestURI()))
                .findFirst().orElseThrow().getHits();
        event.setViews(views);
        return eventMaper.toEventFullDto(event);
    }
}
