package com.efood.service.impl;

import com.efood.dto.FoodRequest;
import com.efood.entity.Category;
import com.efood.entity.Food;
import com.efood.exception.ResourceNotFoundException;
import com.efood.repository.CategoryRepository;
import com.efood.repository.FoodRepository;
import com.efood.service.FoodService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;

    public FoodServiceImpl(FoodRepository foodRepository, CategoryRepository categoryRepository) {
        this.foodRepository = foodRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Food> getAllFoods() {
        return foodRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Food> getAvailableFoods() {
        return foodRepository.findByAvailableTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Food> getPopularFoods() {
        return foodRepository.findTop6ByAvailableTrueOrderByRatingDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Food> getFoodsByCategory(Long categoryId) {
        return foodRepository.findByCategoryIdAndAvailableTrue(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Food getFoodById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with ID: " + id));
    }

    @Override
    public Food saveFood(FoodRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        Food food = new Food();
        food.setName(request.getName().trim());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setImageUrl(request.getImageUrl());
        food.setCategory(category);
        food.setRating(request.getRating() != null ? request.getRating() : 4.5);
        food.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);

        return foodRepository.save(food);
    }

    @Override
    public Food updateFood(Long id, FoodRequest request) {
        Food food = getFoodById(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        food.setName(request.getName().trim());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setImageUrl(request.getImageUrl());
        food.setCategory(category);
        if (request.getRating() != null) {
            food.setRating(request.getRating());
        }
        if (request.getAvailable() != null) {
            food.setAvailable(request.getAvailable());
        }

        return foodRepository.save(food);
    }

    @Override
    public void deleteFood(Long id) {
        Food food = getFoodById(id);
        foodRepository.delete(food);
    }

    @Override
    public void toggleFoodAvailability(Long id) {
        Food food = getFoodById(id);
        food.setAvailable(!Boolean.TRUE.equals(food.getAvailable()));
        foodRepository.save(food);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Food> searchAndFilterFoods(String keyword, Long categoryId, String priceRange, String sort, Boolean availableOnly) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Double minPrice = null;
        Double maxPrice = null;

        if ("below-200".equalsIgnoreCase(priceRange)) {
            maxPrice = 200.0;
        } else if ("200-500".equalsIgnoreCase(priceRange)) {
            minPrice = 200.0;
            maxPrice = 500.0;
        } else if ("above-500".equalsIgnoreCase(priceRange)) {
            minPrice = 500.0;
        }

        Sort sortOrder = Sort.by(Sort.Direction.ASC, "name");
        if ("price-asc".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price-desc".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "price");
        } else if ("rating-desc".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "rating");
        } else if ("newest".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        boolean filterAvailable = availableOnly != null ? availableOnly : false;

        return foodRepository.filterFoods(cleanKeyword, categoryId, filterAvailable, minPrice, maxPrice, sortOrder);
    }
}
