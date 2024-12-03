package ru.practicum.publicapi.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.util.MyPageRequest;
import ru.practicum.entity.dto.category.CategoryDto;
import ru.practicum.entity.exception.NotFoundException;
import ru.practicum.entity.mapper.CategoryMapper;
import ru.practicum.entity.model.Category;
import ru.practicum.entity.repository.CategoriesRepository;
import ru.practicum.publicapi.category.service.PublicCategoriesService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicCategoriesServiceImpl implements PublicCategoriesService {
    private final CategoriesRepository categoriesRepository;

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        MyPageRequest pageable = new MyPageRequest(from, size,
                Sort.by(Sort.Direction.ASC, "id"));
        List<Category> categories = categoriesRepository.findAll(pageable).toList();
        log.info("Получен список всех категорий");
        return CategoryMapper.toDtoList(categories);
    }

    @Override
    public CategoryDto get(Long catId) {
        final Category category = categoriesRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category not found with id = %s", catId)));
        log.info("Get Category: {}", category.getName());
        return CategoryMapper.toDto(category);
    }
}
