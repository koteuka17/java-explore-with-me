package ru.practicum.privateapi.event.service;

import ru.practicum.entity.dto.comment.CommentRequestDto;
import ru.practicum.entity.dto.comment.CommentResponseDto;
import ru.practicum.entity.dto.event.*;
import ru.practicum.entity.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventsService {
    List<EventShortDto> getAll(Long userId, Integer from, Integer size);

    EventFullDto get(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventFullDto create(Long userId, NewEventDto eventDto);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    EventRequestStatusUpdResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdRequest request);

    CommentResponseDto addComment(Long userId, Long eventId, CommentRequestDto commentDto);

    CommentResponseDto updateCommentById(Long userId, Long eventId, Long comId, CommentRequestDto commentDto);

    void deleteCommentById(Long userId, Long eventId, Long comId);
}
