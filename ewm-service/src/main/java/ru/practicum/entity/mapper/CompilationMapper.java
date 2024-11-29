package ru.practicum.entity.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.entity.dto.compilation.CompilationDto;
import ru.practicum.entity.dto.compilation.NewCompilationDto;
import ru.practicum.entity.dto.compilation.UpdateCompilationRequest;
import ru.practicum.entity.model.Compilation;

import java.util.List;
import java.util.stream.Collectors;

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

    public static CompilationDto toDto(Compilation entity) {
        return CompilationDto.builder()
                .id(entity.getId())
                .pinned(entity.isPinned())
                .title(entity.getTitle())
                .events(EventMapper.toEventShortDtoList((entity.getEvents())))
                .build();
    }

    public static List<CompilationDto> toDtoList(List<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::toDto).collect(Collectors.toList());
    }
}
