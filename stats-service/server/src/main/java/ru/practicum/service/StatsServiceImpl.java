package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.ViewsStatsRequest;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end,
                                  List<String> uris, Boolean unique, Integer limit) {
        if (start == null) {
            throw new IllegalArgumentException("Обязательно указывать дату и время начала");
        }
        if (end == null) {
            throw new IllegalArgumentException("Обязательно указывать дату и время окончания");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Неверно указаны даты начала и окончания статистики");
        }
        if (unique) {
            return ViewStatsMapper.toDtoList(repository.findUniqueViewStats(start, end, uris, limit));
        } else {
            return ViewStatsMapper.toDtoList(repository.findViewStats(start, end, uris, limit));
        }
    }

    @Override
    public int getHits(ViewsStatsRequest request) {
        return repository.findUniqueViewStats(request.getStart(), request.getEnd(), request.getUris()).size();
    }

    @Transactional
    @Override
    public EndpointHitDto save(EndpointHitDto dto) {
        dto.setCreated(LocalDateTime.now());
        return EndpointHitMapper.toDto(repository.save(EndpointHitMapper.toEntity(dto)));
    }
}