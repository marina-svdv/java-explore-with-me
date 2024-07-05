package ru.practicum.ewm.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.dto.CategoryMapper;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.category.repository.CategoryRepository;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.CategoryNotEmptyException;
import ru.practicum.ewm.main.exception.CategoryNotFoundException;
import ru.practicum.ewm.main.exception.DuplicateCategoryNameException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        try {
            Category category = new Category();
            category.setName(newCategoryDto.getName());
            Category savedCategory = categoryRepository.save(category);
            return CategoryMapper.toCategoryDto(savedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCategoryNameException("Category name must be unique");
        }
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category with id=" + id + " was not found");
        }
        Category category = categoryRepository.findById(id).orElseThrow();
        if (!category.getName().equals(categoryDto.getName()) && categoryRepository.existsByName(categoryDto.getName())) {
            throw new DuplicateCategoryNameException("Category name must be unique");
        }
        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageRequest).getContent();
        return CategoryMapper.toCategoryDtoList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id=" + catId + " was not found"));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category with id=" + id + " was not found");
        }
        if (eventRepository.existsByCategoryId(id)) {
            throw new CategoryNotEmptyException("The category is not empty");
        }
        categoryRepository.deleteById(id);
    }
}