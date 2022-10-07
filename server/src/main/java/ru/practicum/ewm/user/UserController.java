package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventRequest;
import ru.practicum.ewm.participiation.dto.ParticipationRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    /**
     * СОБЫТИЯ
     */
    /**
     * Добавление события
     */
    @PostMapping("/{userId}/events")
    public EventFullDto addEvents(@RequestBody NewEventDto newEventDto,
                                  @PathVariable int userId) {
        log.info("Запрос на добавление события получен");
        return userService.addEvents(newEventDto, userId);
    }

    /**
     * Получение событий, добавленных текущим пользователем
     */
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUserId(@PathVariable int userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос на вывод события получен");
        return userService.getEventsByUserId(userId, from, size);
    }

    /**
     * Редактирование события
     */
    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@RequestBody UpdateEventRequest updateEventRequest,
                                    @PathVariable int userId) {
        log.info("Запрос на обновление события получен");
        return userService.updateEvent(updateEventRequest, userId);
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем
     **/
    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getAllInfoAboutEvents(@PathVariable @PositiveOrZero int eventId,
                                              @PathVariable @Positive int userId) {
        log.info("Запрос на получение информации о событии получен");
        return userService.getAllInfoAboutEvents(eventId, userId);
    }

    /**
     * Отмена события добавленного текущим пользователем
     **/
    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancelEvents(@PathVariable int eventId,
                                     @PathVariable int userId) {
        log.info("Запрос на поиск события получен");
        return userService.cancelEvents(eventId, userId);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     **/
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsByUser(@PathVariable int userId,
                                                                        @PathVariable int eventId) {
        log.info("Запрос на получение информации о запросах на участие получен");
        return userService.getParticipationRequestsByUser(eventId, userId);
    }

    /**
     * Подтверждение заявки
     */
    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequests(@PathVariable int userId,
                                                   @PathVariable int eventId,
                                                   @PathVariable int reqId) {
        log.info("Запрос на подтверждение заявки получен");
        return userService.confirmRequests(eventId, userId, reqId);
    }

    /**
     * Отмена заявки
     */
    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto cancelRequests(@PathVariable int userId,
                                                  @PathVariable int eventId,
                                                  @PathVariable int reqId) {
        log.info("Запрос на отмену заявки получен");
        return userService.cancelRequests(eventId, userId, reqId);
    }

    /**
     * ЗАПРОСЫ НА УЧАСТИЕ
     */
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserToParticipation(@PathVariable int userId) {
        log.info("Запрос на вывод заявок на участие получен");
        return userService.getRequestsByUserToParticipation(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto createRequestsByUserToParticipation(@PathVariable int userId,
                                                                       @RequestParam int eventId) {
        log.info("Запрос на создание заявки на участие в событии получен");
        return userService.createRequestsByUserToParticipation(userId, eventId);
    }

    /**
     * Отмена своего запроса на участие в событии
     */
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestsByUserToParticipation(@PathVariable int userId,
                                                                       @PathVariable int requestId) {
        log.info("Запрос на отмену заявки на участие в событии получен");
        return userService.cancelRequestsByUserToParticipation(userId, requestId);
    }
}

