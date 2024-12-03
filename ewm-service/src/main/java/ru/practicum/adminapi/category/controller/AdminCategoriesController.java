package ru.practicum.adminapi.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminapi.category.service.AdminCategoriesService;
import ru.practicum.entity.dto.category.CategoryDto;
import ru.practicum.entity.dto.category.NewCategoryDto;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/categories")
public class AdminCategoriesController {
    private final AdminCategoriesService categoriesService;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody @Valid NewCategoryDto dto) {
        log.info("Получен запрос POST /admin/categories c новой категорией: {}", dto.getName());
        return new ResponseEntity<>(categoriesService.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> delete(@PathVariable Long catId) {
        log.info("Получен запрос DELETE /admin/categories/{}", catId);
        categoriesService.delete(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> update(@RequestBody @Valid NewCategoryDto dto,
                                              @PathVariable Long catId) {
        log.info("Получен запрос PATCH /admin/categories/{} на изменение категориии: {}", catId, dto.getName());
        return new ResponseEntity<>(categoriesService.update(dto, catId), HttpStatus.OK);
    }
}
