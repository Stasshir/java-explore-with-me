package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidateException;
import ru.practicum.ewm.participiation.Participation;
import ru.practicum.ewm.participiation.ParticipationRepository;
import ru.practicum.ewm.participiation.dto.ParticipationMaper;
import ru.practicum.ewm.participiation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participiation.dto.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMaper eventMaper;
    private final ParticipationMaper participationMaper;
    private final ParticipationRepository participationRepository;

    @Override
    public EventFullDto addEvents(NewEventDto newEventDto, int userId) {
        Event event = eventMaper.toEvents(newEventDto);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException("Дата события должна быть не раньше чем 2 часа от текущего времени");
        }
        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
        event.setState(State.PENDING);
        event.setCreated(LocalDateTime.now());
        return eventMaper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsByUserId(int userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(from, size);
        return eventRepository.getEventsByInitiator(user, pageable).stream()
                .map(eventMaper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(UpdateEventRequest updateEventRequest, int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Event event = eventRepository.findById(updateEventRequest.getEventId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (event.getState() == State.PUBLISHED) {
            throw new ValidateException("Невозможно обновить опубликованные события");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException("Дата события должна быть не раньше чем 2 часа от текущего времени");
        }
        event.setAnnotation(updateEventRequest.getAnnotation());
        event.setDescription(updateEventRequest.getDescription());
        event.setEventDate(updateEventRequest.getEventDate());
        event.setPaid(updateEventRequest.isPaid());
        event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        event.setTitle(updateEventRequest.getTitle());
        event.setInitiator(user);
        return eventMaper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getAllInfoAboutEvents(int eventId, int userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (!event.getInitiator().equals(user)) {
            throw new ValidateException("Только инициатор имеет доступ к событию");
        }
        return eventMaper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByUser(int eventId, int userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        List<Participation> participationList = participationRepository
                .findParticipationByEvent(event);
        return participationMaper.toListParticipationRequestDto(participationList);
    }

    @Override
    public ParticipationRequestDto confirmRequests(int eventId, int userId, int reqId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        Participation participation = participationRepository
                .findParticipationByIdAndEvent(reqId, event)
                .orElseThrow(() -> new NotFoundException("Заявка на участие не найдена"));
        if (event.getParticipantLimit() == 0) {
            participation.setStatus(Status.CONFIRMED);
        } else {
            int countRequests = participationRepository.countAllByIdAndStatus(reqId, Status.PENDING);
            if (countRequests >= event.getParticipantLimit()) {
                List<Participation> pStatusPending = participationRepository.findAllByIdAndStatus(reqId, Status.PENDING);
                pStatusPending.forEach(p -> p.setStatus(Status.REJECTED));
                throw new ValidateException("Достигнуто максимальное количество участников");
            } else if (countRequests < event.getParticipantLimit()) {
                participation.setStatus(Status.CONFIRMED);
            }
        }
        return participationMaper
                .toParticipationRequestDto(participationRepository.save(participation));
    }

    @Override
    public ParticipationRequestDto cancelRequests(int eventId, int userId, int reqId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        Participation participation = participationRepository
                .findParticipationByIdAndEvent(reqId, event)
                .orElseThrow(() -> new NotFoundException("Заявка на участие не найдена"));
        participation.setStatus(Status.REJECTED);
        return participationMaper.toParticipationRequestDto(participation);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserToParticipation(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return participationMaper
                .toListParticipationRequestDto(participationRepository.findParticipationByRequester(user));
    }

    @Override
    public ParticipationRequestDto createRequestsByUserToParticipation(int userId, int eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getState() != State.PUBLISHED) {
            throw new ValidateException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getInitiator().equals(user)) {
            throw new ValidateException("Нельзя добавить запрос на участие в своем мероприятии");
        }
        if (participationRepository.existsParticipationByRequesterAndEvent(user, event)) {
            throw new ValidateException("Нельзя подавать повторные запросы на участие");
        }
        Participation participation = new Participation();
        participation.setRequester(user);
        participation.setEvent(event);
        participation.setCreated(LocalDateTime.now());
        if (event.isRequestModeration()) {
            participation.setStatus(Status.PENDING);
        } else {
            participation.setStatus(Status.CONFIRMED);
        }
        return participationMaper
                .toParticipationRequestDto(participationRepository.save(participation));
    }

    @Override
    public ParticipationRequestDto cancelRequestsByUserToParticipation(int userId, int requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Participation participation = participationRepository
                .findParticipationByIdAndRequester(requestId, user)
                .orElseThrow(() -> new NotFoundException("Заявка на участие не найдена"));
        participation.setStatus(Status.CANCELED);
        return participationMaper.toParticipationRequestDto(participation);
    }

    @Override
    public EventFullDto cancelEvents(int eventId, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getState() != State.PENDING) {
            throw new ValidateException("Отменить событие можно только в состоянии ожидания модерации");
        }
        if (event.getInitiator() != user) {
            throw new ValidateException("Только инициатор может отменить событие");
        }
        event.setState(State.CANCELED);
        return eventMaper.toEventFullDto(eventRepository.save(event));
    }


}
