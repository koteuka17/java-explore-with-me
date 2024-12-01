package ru.practicum.adminapi.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.EventStatisticsService;
import ru.practicum.entity.dto.compilation.CompilationDto;
import ru.practicum.entity.dto.compilation.NewCompilationDto;
import ru.practicum.entity.dto.compilation.UpdateCompilationRequest;
import ru.practicum.entity.exception.ConflictException;
import ru.practicum.entity.exception.NotFoundException;
import ru.practicum.entity.mapper.CompilationMapper;
import ru.practicum.entity.model.Compilation;
import ru.practicum.entity.model.Event;
import ru.practicum.entity.repository.CompilationRepository;
import ru.practicum.entity.repository.EventRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventStatisticsService statisticsService;

    @Transactional
    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toEntity(newCompilationDto);
        compilation.setEvents(findEvents(newCompilationDto.getEvents()));
        try {
            compilation = compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("Add compilation: {}", compilation.getTitle());
        return CompilationMapper.toDto(compilation, getEventViews(compilation));
    }

    @Transactional
    @Override
    public void delete(Long compId) {
        log.info("Deleted compilation with id = {}", compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        Compilation compilationTarget = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation not found with id = %s", compId)));

        BeanUtils.copyProperties(dto, compilationTarget, "events", "pinned", "title");

        compilationTarget.setEvents(findEvents(dto.getEvents()));
        try {
            compilationRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }

        log.info("Update category: {}", compilationTarget.getTitle());
        return CompilationMapper.toDto(compilationTarget, getEventViews(compilationTarget));
    }

    private Set<Event> findEvents(Set<Long> eventsId) {
        if (eventsId == null) {
            return Set.of();
        }
        return eventRepository.findAllByIdIn(eventsId);
    }

    private Map<Long, Long> getEventViews(Compilation compilation) {
        Set<Event> events = new HashSet<>(compilation.getEvents());
        Map<Long, Long> eventViews = new HashMap<>();
        for (Event event : events) {
            Long views = statisticsService.getEventViews(event);
            eventViews.put(event.getId(), views);
        }
        return eventViews;
    }
}
