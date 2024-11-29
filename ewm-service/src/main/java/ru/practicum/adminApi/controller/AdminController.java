package ru.practicum.adminApi.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminApi.dto.RequestParamForEvent;
import ru.practicum.adminApi.service.category.AdminCategoriesService;
import ru.practicum.adminApi.service.compilation.AdminCompilationService;
import ru.practicum.adminApi.service.event.AdminEventService;
import ru.practicum.adminApi.service.user.AdminUserService;
import ru.practicum.entity.dto.category.CategoryDto;
import ru.practicum.entity.dto.category.NewCategoryDto;
import ru.practicum.entity.dto.compilation.CompilationDto;
import ru.practicum.entity.dto.compilation.NewCompilationDto;
import ru.practicum.entity.dto.compilation.UpdateCompilationRequest;
import ru.practicum.entity.dto.event.EventFullDto;
import ru.practicum.entity.dto.event.UpdateEventAdminRequest;
import ru.practicum.entity.dto.user.NewUserRequest;
import ru.practicum.entity.dto.user.UserDto;
import ru.practicum.entity.dto.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {
    public final AdminCategoriesService categoriesService;
    private final AdminCompilationService compilationService;
    public final AdminEventService eventsService;
    private final AdminUserService userService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> create(@RequestBody @Valid NewCategoryDto dto) {
        log.info("Получен запрос POST /admin/categories c новой категорией: {}", dto.getName());
        return new ResponseEntity<>(categoriesService.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long catId) {
        log.info("Получен запрос DELETE /admin/categories/{}", catId);
        categoriesService.delete(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> update(@RequestBody @Valid NewCategoryDto dto,
                                              @PathVariable Long catId) {
        log.info("Получен запрос PATCH /admin/categories/{} на изменение категориии: {}", catId, dto.getName());
        return new ResponseEntity<>(categoriesService.update(dto, catId), HttpStatus.OK);
    }

    @PostMapping("/compilations")
    public ResponseEntity<CompilationDto> save(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Получен запрос POST /admin/compilations c новой подборкой: {}", newCompilationDto.getTitle());
        return new ResponseEntity<>(compilationService.save(newCompilationDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Void> deleteComp(@PathVariable Long compId) {
        log.info("Получен запрос DELETE /admin/compilations/{}", compId);
        compilationService.delete(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> update(@PathVariable Long compId,
                                                 @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получен запрос PATCH /admin/compilations/{} на изменение подборки.", compId);
        return new ResponseEntity<>(compilationService.update(compId, updateCompilationRequest), HttpStatus.OK);
    }


    @GetMapping("/events")
    public ResponseEntity<List<EventFullDto>> getAll(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(required = false) List<String> states,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) LocalDateTime rangeStart,
                                                     @RequestParam(required = false) LocalDateTime rangeEnd,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET /admin/events");

        List<State> statesEnum = null;
        if (states != null) {
            statesEnum = states.stream().map(State::from).filter(Objects::nonNull).collect(Collectors.toList());
        }

        RequestParamForEvent param = RequestParamForEvent.builder()
                .users(users)
                .states(statesEnum)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        return new ResponseEntity<>(eventsService.getAllByParams(param), HttpStatus.OK);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable Long eventId,
                                               @RequestBody @Valid UpdateEventAdminRequest updateEvent) {
        log.info("Получен запрос PATCH /admin/events/{} на изменение события.", eventId);
        return new ResponseEntity<>(eventsService.update(eventId, updateEvent), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAll(@RequestParam(required = false) List<Long> ids,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET /admin/users");
        return new ResponseEntity<>(userService.getAll(ids, from, size), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> save(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Получен запрос POST /admin/users c новым пользователем: {}", newUserRequest.getEmail());
        return new ResponseEntity<>(userService.save(newUserRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Получен запрос DELETE /admin/users/{}", userId);
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
