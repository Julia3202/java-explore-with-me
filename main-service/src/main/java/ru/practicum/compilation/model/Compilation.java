package ru.practicum.compilation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Table(name = "compilations")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Long id;

    @Column
    private String title;

    @Column
    private Boolean pinned;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "events_compilations",
            inverseJoinColumns = {@JoinColumn(name = "event_id")},
            joinColumns = {@JoinColumn(name = "compilation_id")})
    private List<Event> events;
}
