package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ViewStatsMapper {

    public static ViewStatsDto toDto(ViewStats entity) {
        return ViewStatsDto.builder()
                .app(entity.getApp())
                .uri(entity.getUri())
                .hits(entity.getHits())
                .build();
    }

    public static List<ViewStatsDto> toDtoList(List<ViewStats> viewStats) {
        return viewStats.stream().map(ViewStatsMapper::toDto).collect(Collectors.toList());
    }
}