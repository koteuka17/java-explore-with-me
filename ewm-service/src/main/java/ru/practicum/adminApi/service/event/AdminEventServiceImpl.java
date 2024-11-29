package ru.practicum.adminApi.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adminApi.dto.RequestParamForEvent;
import ru.practicum.entity.util.MyPageRequest;
import ru.practicum.entity.util.UtilMergeProperty;
import ru.practicum.entity.dto.enums.AdminStateAction;
import ru.practicum.entity.dto.event.EventFullDto;
import ru.practicum.entity.dto.event.UpdateEventAdminRequest;
import ru.practicum.entity.exception.ConflictException;
import ru.practicum.entity.exception.NotFoundException;
import ru.practicum.entity.mapper.EventMapper;
import ru.practicum.entity.model.Event;
import ru.practicum.entity.dto.enums.State;
import ru.practicum.entity.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest dto) {
        if (dto.getEventDate() != null) {
            checkEventDate(dto.getEventDate());
        }
        Event eventUpdate = EventMapper.toEntity(dto);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %s не найдено", eventId)));
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Не удается опубликовать событие, потому что оно находится в статусе: PUBLISHED");
        } else if (event.getState().equals(State.CANCELED)) {
            throw new ConflictException("Не удается опубликовать событие, потому что оно находится в статусе: CANCELED");
        } else {
            if (dto.getStateAction() != null) {
                if (dto.getStateAction().toString().equals(AdminStateAction.PUBLISH_EVENT.toString())) {
                    event.setState(State.PUBLISHED);
                }
                if (dto.getStateAction().toString().equals(AdminStateAction.REJECT_EVENT.toString())) {
                    event.setState(State.CANCELED);
                }
            }
        }

        UtilMergeProperty.copyProperties(eventUpdate, event);

        try {
            eventRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("Update event: {}", event.getTitle());
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getAllByParams(RequestParamForEvent param) {
        log.info("Get all events by params");
        MyPageRequest pageable = new MyPageRequest(param.getFrom(), param.getSize(),
                Sort.by(Sort.Direction.ASC, "id"));
        List<Event> events = eventRepository.findEventsByParams(
                param.getUsers(), param.getStates(), param.getCategories(), param.getRangeStart(),
                param.getRangeEnd(), pageable);
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(toList());
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IllegalArgumentException("Field: eventDate. Error: дата и время, на которые запланировано событие," +
                    "не могут быть ранее, чем через час после текущего момента. Value: " + eventDate);
        }
    }
}
