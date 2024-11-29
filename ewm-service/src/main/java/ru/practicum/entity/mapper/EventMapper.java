package ru.practicum.entity.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.entity.dto.event.*;
import ru.practicum.entity.dto.location.LocationDto;
import ru.practicum.entity.model.Event;
import ru.practicum.entity.model.Location;
import ru.practicum.entity.dto.enums.State;

import java.time.LocalDateTime;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@UtilityClass
public class EventMapper {
    public static Event toEntity(NewEventDto dto) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(dto.getDescription())
                .date(dto.getEventDate())
                .location(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()))
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration() == null || dto.getRequestModeration())
                .state(State.PENDING)
                .title(dto.getTitle())
                .build();
    }

    public static Event toEntity(UpdateEventAdminRequest dto) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(dto.getDescription())
                .date(dto.getEventDate())
                .location(dto.getLocation() != null ? new Location(dto.getLocation().getLat(),
                        dto.getLocation().getLon()) : null)
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .build();
    }

    public static Event toEntity(UpdateEventUserRequest dto) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .date(dto.getEventDate())
                .paid(dto.getPaid())
                .location(dto.getLocation() != null ? new Location(dto.getLocation().getLat(),
                        dto.getLocation().getLon()) : null)
                .participantLimit(dto.getParticipantLimit())
                .title(dto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event entity) {
        return EventFullDto.builder()
                .id(entity.getId())
                .annotation(entity.getAnnotation())
                .category(CategoryMapper.toDto(entity.getCategory()))
                .confirmedRequests(entity.getConfirmedRequests())
                .createdOn(entity.getCreatedOn())
                .description(entity.getDescription())
                .eventDate(entity.getDate())
                .initiator(UserMapper.toUserShortDto(entity.getInitiator()))
                .location(new LocationDto(entity.getLocation().getLat(), entity.getLocation().getLon()))
                .paid(entity.getPaid())
                .participantLimit(entity.getParticipantLimit())
                .publishedOn(entity.getPublishedOn())
                .requestModeration(entity.getRequestModeration())
                .state(entity.getState())
                .title(entity.getTitle())
                .views(entity.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event entity) {
        return EventShortDto.builder()
                .id(entity.getId())
                .annotation(entity.getAnnotation())
                .category(CategoryMapper.toDto(entity.getCategory()))
                .confirmedRequests(entity.getConfirmedRequests())
                .eventDate(entity.getDate())
                .initiator(UserMapper.toUserShortDto(entity.getInitiator()))
                .paid(entity.getPaid())
                .title(entity.getTitle())
                .views(entity.getViews())
                .build();
    }

    public static Set<EventShortDto> toEventShortDtoList(Set<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(toSet());
    }
}
