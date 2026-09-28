package com.efood.service;

import com.efood.dto.CategoryRequest;
import com.efood.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    List<Category> getActiveCategories();
    Category getCategoryById(Long id);
    Category saveCategory(CategoryRequest request);
    Category updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    void toggleCategoryStatus(Long id);
    long countFoodsInCategory(Long categoryId);
}
