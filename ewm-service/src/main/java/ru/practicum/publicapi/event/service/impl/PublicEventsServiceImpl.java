package ru.practicum.publicapi.event.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.entity.EventStatisticsService;
import ru.practicum.entity.exception.NotFoundException;
import ru.practicum.entity.model.Event;
import ru.practicum.publicapi.dto.RequestParamForEvent;
import ru.practicum.entity.util.MyPageRequest;
import ru.practicum.entity.dto.enums.State;
import ru.practicum.entity.dto.event.EventFullDto;
import ru.practicum.entity.dto.event.EventShortDto;
import ru.practicum.entity.mapper.EventMapper;
import ru.practicum.entity.model.EventSearchCriteria;
import ru.practicum.entity.repository.EventRepository;
import ru.practicum.publicapi.event.service.PublicEventsService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicEventsServiceImpl implements PublicEventsService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final EventStatisticsService statisticsService;

    @Value("${ewm.service.name}")
    private String serviceName;

    @Transactional
    @Override
    public Set<EventShortDto> getAll(RequestParamForEvent param) {
        MyPageRequest pageable = createPageable(param.getSort(), param.getFrom(), param.getSize());
        EventSearchCriteria eventSearchCriteria = createCriteria(param);
        Set<Event> events = eventRepository.findAllWithFilters(pageable, eventSearchCriteria).toSet();
        Map<Long, Long> eventViews = new HashMap<>();
        for (Event event : events) {
            Long views = statisticsService.getEventViews(event);
            eventViews.put(event.getId(), views);
        }
        Set<EventShortDto> eventShorts = EventMapper.toEventShortDtoList(events, eventViews);
        log.info("Get events list size: {}", eventShorts.size());
        if (eventShorts.isEmpty()) {
            throw new IllegalArgumentException("Событий не найдено");
        }
        saveEndpointHit(param.getRequest());
        return eventShorts;
    }

    @Transactional
    @Override
    public EventFullDto get(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %s не найдено", id)));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException(String.format("Событие с id=%d не опубликовано", id));
        }
        log.info("Get event: {}", event.getId());
        saveEndpointHit(request);
        eventRepository.flush();
        return EventMapper.toEventFullDto(event, statisticsService.getEventViews(event));
    }

    private void saveEndpointHit(HttpServletRequest request) {

        EndpointHitDto endpointHit = EndpointHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app(serviceName)
                .created(LocalDateTime.now())
                .build();
        statsClient.save(endpointHit);
    }

    private MyPageRequest createPageable(String sort, int from, int size) {
        MyPageRequest pageable = null;
        if (sort == null || sort.equalsIgnoreCase("EVENT_DATE")) {
            pageable = new MyPageRequest(from, size,
                    Sort.by(Sort.Direction.ASC, "event_date"));
        } else if (sort.equalsIgnoreCase("VIEWS")) {
            pageable = new MyPageRequest(from, size,
                    Sort.by(Sort.Direction.ASC, "views"));
        }
        return pageable;
    }

    private EventSearchCriteria createCriteria(RequestParamForEvent param) {
        return EventSearchCriteria.builder()
                .text(param.getText())
                .categories(param.getCategories())
                .rangeEnd(param.getRangeEnd())
                .rangeStart(param.getRangeStart())
                .paid(param.getPaid())
                .build();
    }

}
