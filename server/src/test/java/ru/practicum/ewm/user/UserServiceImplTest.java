package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.events.dto.UpdateEventRequest;
import ru.practicum.ewm.participiation.Participation;
import ru.practicum.ewm.participiation.ParticipationRepository;
import ru.practicum.ewm.participiation.dto.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@Transactional(propagation = Propagation.REQUIRED)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserService userService;
    private final CategoryRepository categoryRepository;

    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final EventRepository eventRepository;

    @MockBean
    private final ParticipationRepository participationRepository;

    Category category = Category.builder().id(1).name("category").build();
    User user1 = User.builder().id(1).name("user1").email("user1@mail.ru").build();
    User user2 = User.builder().id(2).name("user2").email("user2@mail.ru").build();
    Event event = Event.builder().id(1).annotation("annotation")
            .eventDate(LocalDateTime.now().plusDays(1)).created(LocalDateTime.now().minusDays(1))
            .description("desc").title("title").state(State.PENDING).initiator(user1).build();
    Event eventPublish = Event.builder().id(2).annotation("annotation")
            .eventDate(LocalDateTime.now().plusDays(1)).created(LocalDateTime.now().minusDays(1))
            .description("desc").title("title").state(State.PUBLISHED).initiator(user2).build();
    NewEventDto newEventDto = NewEventDto.builder().annotation("annotationDto")
            .eventDate(LocalDateTime.now().plusDays(1)).category(1)
            .description("descDto").title("titleDto").build();

    UpdateEventRequest updateEventRequest = UpdateEventRequest.builder().annotation("annotationDto")
            .eventDate(LocalDateTime.now().plusDays(1)).category(1)
            .description("descDto").title("titleDto").build();

    Participation participation = Participation.builder().id(1).event(event).requester(user1)
            .status(Status.CONFIRMED).created(LocalDateTime.now()).build();


    @Test
    void addEvents_shouldBeOk() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user1));
        when(eventRepository.save(any())).thenReturn(event);
        categoryRepository.save(category);
        EventFullDto savedEventFullDto = userService.addEvents(newEventDto, 1);
        assertEquals(savedEventFullDto.getAnnotation(), event.getAnnotation(),
                "События не совпадают");
    }

    @Test
    void getEventsByUserId_shouldBeOk() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user1));
        when(eventRepository.getEventsByInitiator(any(), any())).thenReturn(new PageImpl<>(List.of(event)));
        var result = userService.getEventsByUserId(1, 0, 10);
        assertEquals(result.size(), 1, "Количество событий не совпадает");
        assertEquals(result.get(0).getAnnotation(), event.getAnnotation(), "События не совпадают");
    }

    @Test
    void updateEvent_shouldBeOk() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user1));
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any())).thenReturn(event);
        var result = userService.updateEvent(updateEventRequest, 1);
        assertNotNull(result);
        Mockito.verify(eventRepository, Mockito.times(1))
                .save(event);
        assertEquals(result.getAnnotation(), event.getAnnotation(), "События не совпадают");
    }

    @Test
    void getAllInfoAboutEvents_shouldBeOk() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user1));
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        var result = userService.getAllInfoAboutEvents(1, 1);
        assertNotNull(result);
        assertEquals(result.getAnnotation(), event.getAnnotation(), "События не совпадают");
    }

    @Test
    void getParticipationRequestsByUser_shouldBeOk() {
        when(participationRepository.findParticipationByEvent(event)).thenReturn(List.of(participation));
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        var result = userService.getParticipationRequestsByUser(1, 1);
        assertNotNull(result);
        assertEquals(result.size(), 1, "Количество заявок не совпадает");
    }

    @Test
    void confirmRequests_shouldBeOk() {
        when(participationRepository.findParticipationByIdAndEvent(anyInt(), any()))
                .thenReturn(Optional.ofNullable(participation));
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(participationRepository.save(any())).thenReturn(participation);
        var result = userService.confirmRequests(1, 1, 1);
        assertNotNull(result);
        assertEquals(result.getStatus(), Status.CONFIRMED, "Статусы заявок не совпадают");
        Mockito.verify(participationRepository, Mockito.times(1))
                .save(participation);
    }

    @Test
    void cancelRequests_shouldBeOk() {
        when(participationRepository.findParticipationByIdAndEvent(anyInt(), any()))
                .thenReturn(Optional.ofNullable(participation));
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        var result = userService.cancelRequests(1, 1, 1);
        assertNotNull(result);
        assertEquals(result.getStatus(), Status.REJECTED, "Статусы заявок не совпадают");
    }

    @Test
    void getRequestsByUserToParticipation_shouldBeOk() {
        when(participationRepository.findParticipationByRequester(any()))
                .thenReturn(List.of(participation));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user1));
        var result = userService.getRequestsByUserToParticipation(1);
        assertNotNull(result);
        assertEquals(result.size(), 1, "Количество заявок не совпадают");
    }

    @Test
    void createRequestsByUserToParticipation_shouldBeOk() {
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(eventPublish));
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user1));
        when(participationRepository.save(any()))
                .thenReturn(participation);
        var result = userService.createRequestsByUserToParticipation(1, 1);
        assertEquals(result.getStatus(), Status.CONFIRMED, "Статусы заявок не совпадают");
    }

    @Test
    void cancelRequestsByUserToParticipation_shouldBeOk() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user1));
        when(participationRepository.findParticipationByIdAndRequester(anyInt(), any()))
                .thenReturn(Optional.ofNullable(participation));
        var result = userService.cancelRequestsByUserToParticipation(1, 1);
        assertEquals(result.getStatus(), Status.CANCELED, "Статусы заявок не совпадают");
    }

    @Test
    void cancelEvents_shouldBeOk() {
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any())).thenReturn(event);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user1));
        var result = userService.cancelEvents(1, 1);
        assertEquals(result.getState(), State.CANCELED, "Статусы событий не совпадают");
    }

}