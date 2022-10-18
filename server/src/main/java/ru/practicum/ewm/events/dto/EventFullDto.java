package ru.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.Comments.Comment;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private int id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private String created;
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private String title;
    private State state;
    private int views;
    private Set<Comment> comments;
}
