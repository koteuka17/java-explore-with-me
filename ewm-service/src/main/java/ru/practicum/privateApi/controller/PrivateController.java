package ru.practicum.privateApi.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.entity.dto.enums.Status;
import ru.practicum.entity.dto.event.*;
import ru.practicum.entity.dto.request.ParticipationRequestDto;
import ru.practicum.entity.exception.ConflictException;
import ru.practicum.privateApi.service.event.PrivateEventsService;
import ru.practicum.privateApi.service.request.PrivateRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}")
public class PrivateController {
    public final PrivateEventsService eventsService;
    private final PrivateRequestService requestService;

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getAllEvents(@PathVariable Long userId,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET /users/{}/events c параметрами: from = {}, size = {}", userId, from, size);
        return new ResponseEntity<>(eventsService.getAll(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        log.info("Получен запрос GET /users/{}/events/{}", userId, eventId);
        return new ResponseEntity<>(eventsService.get(userId, eventId), HttpStatus.OK);
    }

    @GetMapping("/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        log.info("Получен запрос GET /users/{}/events/{}/requests", userId, eventId);
        return new ResponseEntity<>(eventsService.getRequests(userId, eventId), HttpStatus.OK);
    }

    @PostMapping("/events")
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @RequestBody @Valid NewEventDto eventDto) {
        log.info("Получен запрос POST /users/{}/events c новым событием: {}", userId, eventDto);
        return new ResponseEntity<>(eventsService.create(userId, eventDto), HttpStatus.CREATED);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                                    @RequestBody @Valid UpdateEventUserRequest eventDto) {
        log.info("Получен запрос PATCH /users/{}/events/{eventId}" +
                " c обновлённым событием id = {}: {}", userId, eventId, eventDto);
        return new ResponseEntity<>(eventsService.update(userId, eventId, eventDto), HttpStatus.OK);
    }

    @PatchMapping("/events/{eventId}/requests")
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

    @GetMapping("/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable Long userId) {
        log.info("Получен запрос GET /users/{}/requests", userId);
        return new ResponseEntity<>(requestService.getRequests(userId), HttpStatus.OK);
    }

    @PostMapping("/requests")
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable Long userId,
                                                                 @RequestParam(defaultValue = "-1") Long eventId) {
        log.info("Получен запрос POST /users/{}/requests c новым запросом на участие в Event с id = {}", userId, eventId);
        return new ResponseEntity<>(requestService.create(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/requests/{requestsId}/cancel")
    public ResponseEntity<ParticipationRequestDto> updRequest(@PathVariable Long userId,
                                                              @PathVariable Long requestsId) {
        log.info("Получен запрос PATCH /users/{}/requests/{requestsId}/cancel" +
                " c отменой запроса id = {}", userId, requestsId);
        return new ResponseEntity<>(requestService.update(userId, requestsId), HttpStatus.OK);
    }
}
