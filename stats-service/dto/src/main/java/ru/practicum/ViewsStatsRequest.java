package ru.practicum;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewsStatsRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private String uris;
    private Boolean unique;
}