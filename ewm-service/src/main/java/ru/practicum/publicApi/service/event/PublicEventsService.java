package ru.practicum.publicApi.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.publicApi.dto.RequestParamForEvent;
import ru.practicum.entity.dto.event.EventFullDto;
import ru.practicum.entity.dto.event.EventShortDto;

import java.util.Set;

public interface PublicEventsService {
    Set<EventShortDto> getAll(RequestParamForEvent param);

    EventFullDto get(Long id, HttpServletRequest request);
}
