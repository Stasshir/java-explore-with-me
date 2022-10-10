package ru.practicum.ewm.compilation;

import lombok.*;
import ru.practicum.ewm.events.Event;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compilation", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private boolean pinned;
    @Column(length = 500)
    private String title;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "comp_events",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "comp_id"))
    private Set<Event> events;


}

