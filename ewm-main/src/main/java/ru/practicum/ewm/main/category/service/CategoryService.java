package ru.practicum.ewm.main.category.service;

import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(Long catId);

    void deleteCategory(Long catId);
}