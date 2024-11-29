package ru.practicum.entity.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.entity.dto.request.ParticipationRequestDto;
import ru.practicum.entity.model.Event;
import ru.practicum.entity.model.Request;
import ru.practicum.entity.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.entity.dto.enums.Status.CONFIRMED;
import static ru.practicum.entity.dto.enums.Status.PENDING;

@UtilityClass
public class RequestMapper {
    public static Request toRequest(Event event, User requester) {
        return Request.builder()
                .requester(requester)
                .event(event)
                .created(LocalDateTime.now())
                .status(event.getRequestModeration() ? PENDING : CONFIRMED)
                .build();
    }

    public static ParticipationRequestDto toParticipationRequestDto(Request entity) {
        return ParticipationRequestDto.builder()
                .id(entity.getId())
                .requester(entity.getRequester().getId())
                .created(entity.getCreated())
                .event(entity.getEvent().getId())
                .status(entity.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> toDtoList(List<Request> requests) {
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(toList());
    }
}
