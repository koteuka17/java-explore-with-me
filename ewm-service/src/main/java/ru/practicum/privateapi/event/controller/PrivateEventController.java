package ru.practicum.privateapi.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.entity.dto.enums.Status;
import ru.practicum.entity.dto.event.*;
import ru.practicum.entity.dto.request.ParticipationRequestDto;
import ru.practicum.entity.exception.ConflictException;
import ru.practicum.privateapi.event.service.PrivateEventsService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    public final PrivateEventsService eventsService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAll(@PathVariable Long userId,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET /users/{}/events c параметрами: from = {}, size = {}", userId, from, size);
        return new ResponseEntity<>(eventsService.getAll(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> get(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        log.info("Получен запрос GET /users/{}/events/{}", userId, eventId);
        return new ResponseEntity<>(eventsService.get(userId, eventId), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        log.info("Получен запрос GET /users/{}/events/{}/requests", userId, eventId);
        return new ResponseEntity<>(eventsService.getRequests(userId, eventId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> create(@PathVariable Long userId,
                                                    @RequestBody @Valid NewEventDto eventDto) {
        log.info("Получен запрос POST /users/{}/events c новым событием: {}", userId, eventDto);
        return new ResponseEntity<>(eventsService.create(userId, eventDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable Long userId, @PathVariable Long eventId,
                                                    @RequestBody @Valid UpdateEventUserRequest eventDto) {
        log.info("Получен запрос PATCH /users/{}/events/{eventId}" +
                " c обновлённым событием id = {}: {}", userId, eventId, eventDto);
        return new ResponseEntity<>(eventsService.update(userId, eventId, eventDto), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdResult> updateRequestStatus(@PathVariable Long userId,
                                                                           @PathVariable Long eventId,
                                                                           @RequestBody EventRequestStatusUpdRequest request) {
        log.info("Получен запрос PATCH /users/{}/events/{}/requests" +
                " на обновление статуса события id = {}: {}", userId, eventId, eventId, request);
        if (Status.from(request.getStatus()) == null) {
            throw new ConflictException("Status is not validate");
        }
        return new ResponseEntity<>(eventsService.updateRequestStatus(userId, eventId, request), HttpStatus.OK);
    }
}
