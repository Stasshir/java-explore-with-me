package ru.practicum.ewm.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Page<Event> getEventsByInitiator(User initiator, Pageable p);

    Optional<Event> findEventByIdAndState(int id, State state);

    @Query(nativeQuery = true, value = "select * from events as e where (e.initiator_id IN ?1) " +
            "and (e.state IN ?2) and (e.category_id IN ?3) and (e.event_date > ?4) and (e.event_date < ?5)")
    Page<Event> findEvents(int[] users, String[] states, int[] categories, LocalDateTime start,
                           LocalDateTime end, Pageable p);

    @Query(nativeQuery = true, value = "select * from events as e where ((e.annotation like ?1) " +
            "or (e.description like ?1)) and (e.category_id IN ?2) and (e.paid = ?3) " +
            "and (e.event_date > ?4) and (e.event_date < ?5) and (e.state = 'PUBLISHED')")
    Page<Event> getEvents(String text, int[] categories, boolean paid, LocalDateTime start,
                          LocalDateTime end, Pageable p);
}
