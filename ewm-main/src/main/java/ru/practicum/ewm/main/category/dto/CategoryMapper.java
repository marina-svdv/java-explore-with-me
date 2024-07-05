package ru.practicum.ewm.main.category.dto;

import ru.practicum.ewm.main.category.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static Category toCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        return new Category(categoryDto.getId(), categoryDto.getName());
    }

    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDto(category.getId(), category.getName());
    }

    public static List<Category> toCategoryList(List<CategoryDto> categoryDtos) {
        return categoryDtos.stream()
                .map(CategoryMapper::toCategory)
                .collect(Collectors.toList());
    }

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}