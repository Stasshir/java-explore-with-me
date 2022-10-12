package ru.practicum.ewm.events;

import lombok.*;
import ru.practicum.ewm.Comments.Comment;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.participiation.Participation;
import ru.practicum.ewm.participiation.dto.Status;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 1000, nullable = false)
    private String annotation;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany()
    @JoinColumn(name = "event_id")
    private List<Participation> participationList = new ArrayList<>();

    private LocalDateTime created;
    @Column(length = 1000)
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne()
    @JoinColumn(name = "initiator_id")
    private User initiator;

    private double lat;
    private double lon;
    private boolean paid;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private boolean requestModeration;
    @Column(length = 500)
    private String title;

    @Enumerated(EnumType.STRING)
    private State state;

    private int views;

    @ManyToMany(mappedBy = "events")
    private Set<Compilation> compilations;

    @OneToMany
    @JoinColumn(name = "event_id")
    private Set<Comment> comments;

    //возвращает количество одобренных заявок
    public int getConfirmedRequests() {
        if (participationList == null) return 0;
        return (int) participationList.stream()
                .filter(participation -> participation.getStatus() == Status.CONFIRMED)
                .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id && paid == event.paid && participantLimit == event.participantLimit && requestModeration == event.requestModeration && views == event.views && Objects.equals(annotation, event.annotation) && Objects.equals(category, event.category) && Objects.equals(created, event.created) && Objects.equals(description, event.description) && Objects.equals(eventDate, event.eventDate) && Objects.equals(initiator, event.initiator) && Objects.equals(publishedOn, event.publishedOn) && Objects.equals(title, event.title) && state == event.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, category, created, description, eventDate, initiator, paid, participantLimit, publishedOn, requestModeration, title, state, views);
    }
}
