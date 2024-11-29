package ru.practicum.privateApi.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.dto.enums.State;
import ru.practicum.entity.dto.enums.Status;
import ru.practicum.entity.dto.request.ParticipationRequestDto;
import ru.practicum.entity.exception.ConflictException;
import ru.practicum.entity.exception.NotFoundException;
import ru.practicum.entity.mapper.RequestMapper;
import ru.practicum.entity.model.Event;
import ru.practicum.entity.model.Request;
import ru.practicum.entity.model.User;
import ru.practicum.entity.repository.EventRepository;
import ru.practicum.entity.repository.RequestRepository;
import ru.practicum.entity.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        if (userRepository.existsById(userId)) {
            return RequestMapper.toDtoList(requestRepository.findAllByRequesterId(userId));
        } else {
            throw new NotFoundException(String.format("Пользователь с id = %s не найден", userId));
        }
    }

    @Transactional
    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        if (eventId == -1) {
            throw new IllegalArgumentException("В запросе пропущен обязательный параметр eventId");
        }
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %s не найдено", eventId)));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));

        Long participantLimit = event.getParticipantLimit();
        Long confirmedRequests = event.getConfirmedRequests();
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException(String.format("Запрос с requesterId=%d и eventId=%d уже существует", userId, eventId));
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь с id=%d не должен быть организатором", userId));
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException(String.format("Событие с id=%d не опубликовано", eventId));
        }
        if (participantLimit > 0 && participantLimit.equals(confirmedRequests)) {
            throw new ConflictException(String.format("В событии с id=%d достигнут лимит участников", eventId));
        }
        if (participantLimit == 0) {
            event.setRequestModeration(false);
        }
        if (!event.getRequestModeration()) {
            event.setConfirmedRequests(confirmedRequests + 1);
            eventRepository.save(event);
        }
        return RequestMapper.toParticipationRequestDto(requestRepository.save(RequestMapper.toRequest(event, user)));
    }

    @Transactional
    @Override
    public ParticipationRequestDto update(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id=%d " +
                        "и requesterId=%d не найден", requestId, userId)));
        request.setStatus(Status.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
