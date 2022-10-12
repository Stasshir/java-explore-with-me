package ru.practicum.ewm.Comments;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.user.User;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByEventOrderByTimestamp(Event event);

    List<Comment> findAllByUserOrderByTimestamp(User user);
}
