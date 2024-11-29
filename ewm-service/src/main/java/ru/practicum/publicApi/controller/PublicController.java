package ru.practicum.publicApi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.entity.dto.category.CategoryDto;
import ru.practicum.entity.dto.compilation.CompilationDto;
import ru.practicum.entity.dto.event.EventFullDto;
import ru.practicum.entity.dto.event.EventShortDto;
import ru.practicum.publicApi.dto.RequestParamForEvent;
import ru.practicum.publicApi.service.category.PublicCategoriesService;
import ru.practicum.publicApi.service.compilation.PublicCompilationsService;
import ru.practicum.publicApi.service.event.PublicEventsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class PublicController {
    public final PublicCategoriesService categoriesService;
    public final PublicCompilationsService compilationsService;
    public final PublicEventsService eventsService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam(defaultValue = "0") int from,
                                                              @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /categories c параметрами: from = {}, size = {}", from, size);
        return new ResponseEntity<>(categoriesService.getAll(from, size), HttpStatus.OK);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long catId) {
        log.info("Получен запрос GET /categories/{}", catId);
        return new ResponseEntity<>(categoriesService.get(catId), HttpStatus.OK);
    }

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationDto>> getAllComp(@RequestParam(required = false) Boolean pinned,
                                                           @RequestParam(defaultValue = "0") int from,
                                                           @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /compilations c параметрами: pinned = {}, from = {}, size = {}", pinned, from, size);
        return new ResponseEntity<>(compilationsService.getAll(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/compilations/{comId}")
    public ResponseEntity<CompilationDto> getComp(@PathVariable Long comId) {
        log.info("Получен запрос GET /compilations/{}", comId);
        return new ResponseEntity<>(compilationsService.get(comId), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<Set<EventShortDto>> getAllEvents(@RequestParam(required = false) String text,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) Boolean paid,
                                                     @RequestParam(required = false) LocalDateTime rangeStart,
                                                     @RequestParam(required = false) LocalDateTime rangeEnd,
                                                     @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                     @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                     @RequestParam(defaultValue = "10") @Positive int size,
                                                     HttpServletRequest request) {
        log.info("Получен запрос GET /events c параметрами: text = {}, categories = {}, paid = {}, rangeStart = {}, " +
                        "rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}", text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        RequestParamForEvent param = RequestParamForEvent.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .request(request)
                .build();
        return new ResponseEntity<>(eventsService.getAll(param), HttpStatus.OK);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получен запрос GET /events/{}", id);
        return new ResponseEntity<>(eventsService.get(id, request), HttpStatus.OK);
    }
}
