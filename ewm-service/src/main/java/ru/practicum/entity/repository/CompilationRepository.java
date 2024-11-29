package ru.practicum.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.util.MyPageRequest;
import ru.practicum.entity.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findAllByPinned(Boolean pinned, MyPageRequest pageable);
}
