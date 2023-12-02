package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
