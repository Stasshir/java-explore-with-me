package ru.practicum.ewm.user;

import ru.practicum.ewm.Comments.dto.CommentDto;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventRequest;
import ru.practicum.ewm.participiation.dto.ParticipationRequestDto;

import java.util.List;

public interface UserService {

    EventFullDto addEvents(NewEventDto newEventDto, int userId);

    List<EventShortDto> getEventsByUserId(int userId, int from, int size);

    EventFullDto updateEvent(UpdateEventRequest updateEventRequest, int userId);

    EventFullDto getAllInfoAboutEvents(int eventId, int userId);

    List<ParticipationRequestDto> getParticipationRequestsByUser(int eventId, int userId);

    ParticipationRequestDto confirmRequests(int eventId, int userId, int reqId);

    ParticipationRequestDto cancelRequests(int eventId, int userId, int reqId);

    List<ParticipationRequestDto> getRequestsByUserToParticipation(int userId);

    ParticipationRequestDto createRequestsByUserToParticipation(int userId, int eventId);

    ParticipationRequestDto cancelRequestsByUserToParticipation(int userId, int requestId);

    EventFullDto cancelEvents(int eventId, int userId);

    CommentDto addComment(int eventId, int userId, CommentDto commentDto);

    void deleteComment(int commentId);

    CommentDto updateComment(int commentId, CommentDto commentDto);

    List<CommentDto> getAllCommentsByEvents(int eventId);

    List<CommentDto> getAllCommentsByUser(int userId);
}
