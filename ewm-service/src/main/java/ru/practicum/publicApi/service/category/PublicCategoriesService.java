package ru.practicum.publicApi.service.category;

import ru.practicum.entity.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoriesService {
    List<CategoryDto> getAll(int from, int size);

    CategoryDto get(Long catId);
}