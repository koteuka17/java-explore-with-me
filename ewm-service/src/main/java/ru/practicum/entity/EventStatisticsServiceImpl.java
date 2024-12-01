package ru.practicum.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.entity.model.Event;
import ru.practicum.model.ViewsStatsRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventStatisticsServiceImpl implements EventStatisticsService {
    private final StatsClient statsClient;

    @Override
    public Long getEventViews(Event event) {
        ViewsStatsRequest request = ViewsStatsRequest.builder()
                .uris(List.of("/events/" + event.getId()))
                .start(event.getPublishedOn())
                .end(LocalDateTime.now())
                .unique(true)
                .build();

        ResponseEntity<Object> response = statsClient.getStats(request);
        return Long.valueOf(Objects.requireNonNull(response.getBody()).toString());
    }
}
