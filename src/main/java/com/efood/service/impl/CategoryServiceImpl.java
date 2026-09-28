package com.efood.service.impl;

import com.efood.dto.CategoryRequest;
import com.efood.entity.Category;
import com.efood.exception.ResourceNotFoundException;
import com.efood.repository.CategoryRepository;
import com.efood.repository.FoodRepository;
import com.efood.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, FoodRepository foodRepository) {
        this.categoryRepository = categoryRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    @Override
    public Category saveCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new IllegalArgumentException("A category with the name '" + request.getName() + "' already exists.");
        }

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setActive(request.getActive() != null ? request.getActive() : true);

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = getCategoryById(id);

        categoryRepository.findByNameIgnoreCase(request.getName().trim())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException("Another category with name '" + request.getName() + "' already exists.");
                    }
                });

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        long foodsCount = foodRepository.countByCategoryId(id);
        if (foodsCount > 0) {
            throw new IllegalStateException("Cannot delete category because " + foodsCount + " food item(s) are assigned to it. You can deactivate it instead.");
        }
        categoryRepository.delete(category);
    }

    @Override
    public void toggleCategoryStatus(Long id) {
        Category category = getCategoryById(id);
        category.setActive(!Boolean.TRUE.equals(category.getActive()));
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFoodsInCategory(Long categoryId) {
        return foodRepository.countByCategoryId(categoryId);
    }
}
