package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.ViewsStatsRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique, Integer limit);

    int getHits(ViewsStatsRequest request);

    EndpointHitDto save(EndpointHitDto dto);
}