package ru.practicum.privateApi.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.EventStatisticsService;
import ru.practicum.entity.util.MyPageRequest;
import ru.practicum.entity.util.UtilMergeProperty;
import ru.practicum.entity.dto.enums.State;
import ru.practicum.entity.dto.enums.UserStateAction;
import ru.practicum.entity.dto.event.*;
import ru.practicum.entity.dto.request.ParticipationRequestDto;
import ru.practicum.entity.exception.ConflictException;
import ru.practicum.entity.exception.NotFoundException;
import ru.practicum.entity.mapper.EventMapper;
import ru.practicum.entity.mapper.RequestMapper;
import ru.practicum.entity.model.Event;
import ru.practicum.entity.model.Request;
import ru.practicum.entity.repository.CategoriesRepository;
import ru.practicum.entity.repository.EventRepository;
import ru.practicum.entity.repository.RequestRepository;
import ru.practicum.entity.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static ru.practicum.entity.dto.enums.Status.CONFIRMED;
import static ru.practicum.entity.dto.enums.Status.REJECTED;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PrivateEventsServiceImpl implements PrivateEventsService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CategoriesRepository categoriesRepository;
    private final EventStatisticsService statisticsService;

    @Override
    public List<EventShortDto> getAll(Long userId, Integer from, Integer size) {
        log.info("Get all events");
        MyPageRequest pageRequest = new MyPageRequest(from, size,
                Sort.by(Sort.Direction.ASC, "id"));
        Map<Long, Long> eventsViews = new HashMap<>();
        List<Event> events = eventRepository.findAll(pageRequest).stream().toList();
        List<EventShortDto> result = new ArrayList<>();
        for (Event event : events) {
            Long views = statisticsService.getEventViews(event);
            eventsViews.put(event.getId(), views);
            result.add(EventMapper.toEventShortDto(event, eventsViews));
        }
        return result;
    }

    @Override
    public EventFullDto get(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id = %s и userId = %s не найдено", eventId, userId)));
        log.info("Get event: {}", event.getId());
        return EventMapper.toEventFullDto(event, statisticsService.getEventViews(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException(
                    String.format("Событие с id = %s и userId = %s не найдено", eventId, userId));
        }
        return RequestMapper.toDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Transactional
    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        checkEventDate(eventDto.getEventDate());
        Event event = EventMapper.toEntity(eventDto);
        event.setCategory(categoriesRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id=%d не найдена",
                        eventDto.getCategory()))));
        event.setPublishedOn(LocalDateTime.now());
        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId))));
        try {
            event = eventRepository.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("Add event: {}", event.getTitle());
        return EventMapper.toEventFullDto(event, 0L);
    }

    @Transactional
    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        Event eventTarget = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id = %s и userId = %s не найдено", eventId, userId)));
        Event eventUpdate = EventMapper.toEntity(eventDto);
        checkEventDate(eventUpdate.getDate());

        if (eventDto.getCategory() != null) {
            eventUpdate.setCategory(categoriesRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Категория с id=%d не найдена",
                            eventDto.getCategory()))));
        }

        if (eventTarget.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие не может быть опубликовано");
        }

        UtilMergeProperty.copyProperties(eventUpdate, eventTarget);
        if (eventDto.getStateAction() != null) {
            if (UserStateAction.CANCEL_REVIEW.toString().equals(eventDto.getStateAction().toString())) {
                eventTarget.setState(State.CANCELED);
            } else if (UserStateAction.SEND_TO_REVIEW.toString().equals(eventDto.getStateAction().toString())) {
                eventTarget.setState(State.PENDING);
            }
        }

        eventRepository.flush();
        log.info("Update event: {}", eventTarget.getTitle());
        return EventMapper.toEventFullDto(eventTarget, statisticsService.getEventViews(eventTarget));
    }

    @Transactional
    @Override
    public EventRequestStatusUpdResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdRequest request) {
        List<ParticipationRequestDto> confirmedRequests = List.of();
        List<ParticipationRequestDto> rejectedRequests = List.of();

        List<Long> requestIds = request.getRequestIds();
        List<Request> requests = requestRepository.findAllByIdIn(requestIds);

        String status = request.getStatus();

        if (status.equals(REJECTED.toString())) {
            boolean isConfirmedRequestExists = requests.stream()
                    .anyMatch(r -> r.getStatus().equals(CONFIRMED));
            if (isConfirmedRequestExists) {
                throw new ConflictException("Невозможно отклонить подтвержденный запрос");
            }
            rejectedRequests = requests.stream()
                    .peek(r -> r.setStatus(REJECTED))
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(toList());
            return new EventRequestStatusUpdResult(confirmedRequests, rejectedRequests);
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id = %s и userId = %s не найдено", eventId, userId)));
        Long participantLimit = event.getParticipantLimit();
        Long approvedRequests = event.getConfirmedRequests();
        long availableParticipants = participantLimit - approvedRequests;
        long potentialParticipants = requestIds.size();

        if (participantLimit > 0 && participantLimit.equals(approvedRequests)) {
            throw new ConflictException(String.format("В событии с id=%d достигнут лимит участников", eventId));
        }

        if (status.equals(CONFIRMED.toString())) {
            if (participantLimit.equals(0L) || (potentialParticipants <= availableParticipants && !event.getRequestModeration())) {
                confirmedRequests = requests.stream()
                        .peek(r -> {
                            if (!r.getStatus().equals(CONFIRMED)) {
                                r.setStatus(CONFIRMED);
                            } else {
                                throw new ConflictException(String.format("Запрос с id=%d уже подтвержден", r.getId()));
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(toList());
                event.setConfirmedRequests(approvedRequests + potentialParticipants);
            } else {
                confirmedRequests = requests.stream()
                        .limit(availableParticipants)
                        .peek(r -> {
                            if (!r.getStatus().equals(CONFIRMED)) {
                                r.setStatus(CONFIRMED);
                            } else {
                                throw new ConflictException(String.format("Запрос с id=%d уже подтвержден", r.getId()));
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(toList());
                rejectedRequests = requests.stream()
                        .skip(availableParticipants)
                        .peek(r -> {
                            if (!r.getStatus().equals(REJECTED)) {
                                r.setStatus(REJECTED);
                            } else {
                                throw new ConflictException(String.format("Запрос с id=%d уже отклонен", r.getId()));
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(toList());
                event.setConfirmedRequests(confirmedRequests.size());
            }
        }
        eventRepository.flush();
        requestRepository.flush();
        return new EventRequestStatusUpdResult(confirmedRequests, rejectedRequests);
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Field: eventDate. Error: дата и время, на которые запланировано событие," +
                    " не могут быть ранее, чем через два часа после текущего момента. Value: " + eventDate);
        }
    }
}
