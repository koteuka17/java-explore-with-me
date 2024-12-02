package ru.practicum.adminapi.event.service;

import ru.practicum.adminapi.dto.RequestParamForEvent;
import ru.practicum.entity.dto.event.EventFullDto;
import ru.practicum.entity.dto.event.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {
    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEvent);

    List<EventFullDto> getAllByParams(RequestParamForEvent param);
}
