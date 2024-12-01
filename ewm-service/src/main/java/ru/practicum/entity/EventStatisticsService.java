package ru.practicum.entity;

import ru.practicum.entity.model.Event;

public interface EventStatisticsService {
    Long getEventViews(Event event);
}
