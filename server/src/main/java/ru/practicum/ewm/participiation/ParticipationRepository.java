package ru.practicum.ewm.participiation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.participiation.dto.Status;
import ru.practicum.ewm.user.User;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    Optional<Participation> findParticipationByEventAndRequester(Event events, User user);

    Optional<Participation> findParticipationByIdAndEventAndRequester(int id, Event events, User user);

    List<Participation> findParticipationByEvent(Event event);

    Optional<Participation> findParticipationByIdAndRequester(int id, User user);

    Optional<Participation> findParticipationByIdAndEvent(int id, Event event);

    int countAllByIdAndStatus(int id, Status status);

    List<Participation> findAllByIdAndStatus(int id, Status status);

    List<Participation> findParticipationByRequester(User user);

    boolean existsParticipationByRequesterAndEvent(User user, Event event);
}
