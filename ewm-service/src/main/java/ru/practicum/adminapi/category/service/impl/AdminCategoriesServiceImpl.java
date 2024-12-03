package ru.practicum.adminapi.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adminapi.category.service.AdminCategoriesService;
import ru.practicum.entity.util.UtilMergeProperty;
import ru.practicum.entity.dto.category.CategoryDto;
import ru.practicum.entity.dto.category.NewCategoryDto;
import ru.practicum.entity.exception.ConditionsNotMetException;
import ru.practicum.entity.exception.ConflictException;
import ru.practicum.entity.exception.NotFoundException;
import ru.practicum.entity.mapper.CategoryMapper;
import ru.practicum.entity.model.Category;
import ru.practicum.entity.repository.CategoriesRepository;
import ru.practicum.entity.repository.EventRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoriesServiceImpl implements AdminCategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto dto) {
        Category category = CategoryMapper.toEntity(dto);
        try {
            category = categoriesRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("Add category: {}", category.getName());
        return CategoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public void delete(Long catId) {
        if (eventRepository.existsByCategory(get(catId))) {
            throw new ConditionsNotMetException("The category is not empty");
        } else  {
            log.info("Deleted category with id = {}", catId);
            categoriesRepository.deleteById(catId);
        }
    }

    @Transactional
    @Override
    public CategoryDto update(NewCategoryDto dto, Long catId) {
        Category categoryUpdate = CategoryMapper.toEntity(dto);
        Category categoryTarget = get(catId);

        try {
            UtilMergeProperty.copyProperties(categoryUpdate, categoryTarget);
            categoriesRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("Update category: {}", categoryTarget.getName());
        return CategoryMapper.toDto(categoryTarget);
    }

    private Category get(Long id) {
        final Category category = categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category not found with id = %s", id)));
        log.info("Get category: {}", category.getName());
        return category;
    }
}
