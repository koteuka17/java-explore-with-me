package ru.practicum.adminApi.service.category;

import ru.practicum.entity.dto.category.CategoryDto;
import ru.practicum.entity.dto.category.NewCategoryDto;

public interface AdminCategoriesService {
    CategoryDto create(NewCategoryDto dto);

    void delete(Long catId);

    CategoryDto update(NewCategoryDto dto, Long catId);
}
