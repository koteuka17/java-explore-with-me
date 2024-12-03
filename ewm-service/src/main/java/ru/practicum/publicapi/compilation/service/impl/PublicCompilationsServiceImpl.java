package ru.practicum.publicapi.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.EventStatisticsService;
import ru.practicum.entity.model.Event;
import ru.practicum.entity.util.MyPageRequest;
import ru.practicum.entity.dto.compilation.CompilationDto;
import ru.practicum.entity.exception.NotFoundException;
import ru.practicum.entity.mapper.CompilationMapper;
import ru.practicum.entity.model.Compilation;
import ru.practicum.entity.repository.CompilationRepository;
import ru.practicum.publicapi.compilation.service.PublicCompilationsService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicCompilationsServiceImpl implements PublicCompilationsService {
    private final CompilationRepository compilationRepository;
    private final EventStatisticsService statisticsService;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        MyPageRequest pageable = new MyPageRequest(from, size,
                Sort.by(Sort.Direction.ASC, "id"));
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).toList();
        }
        Set<Event> events = new HashSet<>();
        for (Compilation c : compilations) {
            events.addAll(c.getEvents());
        }
        Map<Long, Long> eventViews = new HashMap<>();
        for (Event event : events) {
            Long views = statisticsService.getEventViews(event);
            eventViews.put(event.getId(), views);
        }
        log.info("Get list compilations:");
        return CompilationMapper.toDtoList(compilations, eventViews);
    }

    @Override
    public CompilationDto get(Long comId) {
        final Compilation compilation = compilationRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation not found with id = %s", comId)));
        Set<Event> events = new HashSet<>(compilation.getEvents());
        Map<Long, Long> eventViews = new HashMap<>();
        for (Event event : events) {
            Long views = statisticsService.getEventViews(event);
            eventViews.put(event.getId(), views);
        }
        log.info("Get compilation: {}", compilation.getTitle());
        return CompilationMapper.toDto(compilation, eventViews);
    }
}