package ru.practicum.adminapi.compilation.service;

import ru.practicum.entity.dto.compilation.CompilationDto;
import ru.practicum.entity.dto.compilation.NewCompilationDto;
import ru.practicum.entity.dto.compilation.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto save(NewCompilationDto newCompilationDto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
