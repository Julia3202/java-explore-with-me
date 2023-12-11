package ru.practicum.request.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ParticipationRequestDto {

    private long id;

    private String created;

    private Long event;

    private Long requester;

    private String status;
}