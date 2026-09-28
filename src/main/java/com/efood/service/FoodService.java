package com.efood.service;

import com.efood.dto.FoodRequest;
import com.efood.entity.Food;

import java.util.List;

public interface FoodService {
    List<Food> getAllFoods();
    List<Food> getAvailableFoods();
    List<Food> getPopularFoods();
    List<Food> getFoodsByCategory(Long categoryId);
    Food getFoodById(Long id);
    Food saveFood(FoodRequest request);
    Food updateFood(Long id, FoodRequest request);
    void deleteFood(Long id);
    void toggleFoodAvailability(Long id);
    List<Food> searchAndFilterFoods(String keyword, Long categoryId, String priceRange, String sort, Boolean availableOnly);
}
