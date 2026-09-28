package com.efood.controller;

import com.efood.entity.Food;
import com.efood.service.CategoryService;
import com.efood.service.FoodService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FoodController {

    private final FoodService foodService;
    private final CategoryService categoryService;

    public FoodController(FoodService foodService, CategoryService categoryService) {
        this.foodService = foodService;
        this.categoryService = categoryService;
    }

    @GetMapping("/foods")
    public String listFoods(@RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "category", required = false) Long categoryId,
                            @RequestParam(value = "price", required = false) String priceRange,
                            @RequestParam(value = "sort", required = false) String sort,
                            @RequestParam(value = "available", required = false) Boolean availableOnly,
                            Model model) {

        List<Food> foods = foodService.searchAndFilterFoods(keyword, categoryId, priceRange, sort, availableOnly);

        model.addAttribute("foods", foods);
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("selectedPrice", priceRange);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("selectedAvailable", availableOnly);

        return "food/list";
    }

    @GetMapping("/foods/{id}")
    public String foodDetails(@PathVariable("id") Long id, Model model) {
        Food food = foodService.getFoodById(id);
        model.addAttribute("food", food);

        // Fetch related foods in same category
        if (food.getCategory() != null) {
            List<Food> relatedFoods = foodService.getFoodsByCategory(food.getCategory().getId()).stream()
                    .filter(f -> !f.getId().equals(id))
                    .limit(3)
                    .toList();
            model.addAttribute("relatedFoods", relatedFoods);
        }

        return "food/details";
    }
}
