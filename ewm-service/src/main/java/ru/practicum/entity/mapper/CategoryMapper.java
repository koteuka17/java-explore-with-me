package ru.practicum.entity.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.entity.dto.category.CategoryDto;
import ru.practicum.entity.dto.category.NewCategoryDto;
import ru.practicum.entity.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public final class CategoryMapper {

    public static Category toEntity(NewCategoryDto dto) {
        return Category.builder()
                .name(dto.getName()).build();
    }

    public static CategoryDto toDto(Category entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static List<CategoryDto> toDtoList(List<Category> categories) {
        return categories.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }
}