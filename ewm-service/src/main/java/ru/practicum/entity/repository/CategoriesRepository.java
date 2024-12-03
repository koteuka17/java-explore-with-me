package ru.practicum.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.model.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
}
