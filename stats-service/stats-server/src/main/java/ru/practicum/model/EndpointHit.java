package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "endpoint_hits")
@AllArgsConstructor
@Entity
@Getter
@Setter
@NoArgsConstructor
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hits_id")
    private Long id;

    @Column(name = "app", length = 200)
    private String app;

    @Column(name = "uri", length = 200)
    private String uri;

    @Column(name = "ip", length = 200)
    private String ip;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
