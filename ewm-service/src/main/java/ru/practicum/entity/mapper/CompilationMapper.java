package ru.practicum.entity.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.entity.dto.compilation.CompilationDto;
import ru.practicum.entity.dto.compilation.NewCompilationDto;
import ru.practicum.entity.dto.compilation.UpdateCompilationRequest;
import ru.practicum.entity.model.Compilation;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class CompilationMapper {
    public static Compilation toEntity(NewCompilationDto dto) {
        return Compilation.builder()
                .pinned(dto.isPinned())
                .title(dto.getTitle())
                .build();
    }

    public static Compilation toEntity(UpdateCompilationRequest dto) {
        return Compilation.builder()
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static CompilationDto toDto(Compilation entity, Map<Long, Long> eventViews) {
        if (eventViews == null) {
            eventViews = new HashMap<>();
        }
        return CompilationDto.builder()
                .id(entity.getId())
                .pinned(entity.isPinned())
                .title(entity.getTitle())
                .events(EventMapper.toEventShortDtoList(entity.getEvents(), eventViews))
                .build();
    }

    public static List<CompilationDto> toDtoList(List<Compilation> compilations, Map<Long, Long> eventViews) {
        return compilations.stream()
                .map(c -> toDto(c, eventViews))
                .collect(toList());
    }
}
