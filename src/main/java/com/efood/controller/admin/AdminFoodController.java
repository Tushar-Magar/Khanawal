package com.efood.controller.admin;

import com.efood.dto.FoodRequest;
import com.efood.entity.Food;
import com.efood.service.CategoryService;
import com.efood.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/foods")
public class AdminFoodController {

    private final FoodService foodService;
    private final CategoryService categoryService;

    public AdminFoodController(FoodService foodService, CategoryService categoryService) {
        this.foodService = foodService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listFoods(Model model) {
        List<Food> foods = foodService.getAllFoods();
        model.addAttribute("foods", foods);
        return "admin/foods/list";
    }

    @GetMapping("/add")
    public String showAddFoodForm(Model model) {
        if (!model.containsAttribute("foodRequest")) {
            model.addAttribute("foodRequest", new FoodRequest());
        }
        model.addAttribute("categories", categoryService.getActiveCategories());
        return "admin/foods/form";
    }

    @PostMapping("/add")
    public String addFood(@Valid @ModelAttribute("foodRequest") FoodRequest foodRequest,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "admin/foods/form";
        }

        try {
            foodService.saveFood(foodRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Food item added successfully!");
            return "redirect:/admin/foods";
        } catch (Exception e) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/foods/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditFoodForm(@PathVariable("id") Long id, Model model) {
        Food food = foodService.getFoodById(id);
        FoodRequest foodRequest = new FoodRequest(
                food.getId(),
                food.getName(),
                food.getDescription(),
                food.getPrice(),
                food.getCategory() != null ? food.getCategory().getId() : null,
                food.getImageUrl(),
                food.getRating(),
                food.getAvailable()
        );

        model.addAttribute("foodRequest", foodRequest);
        model.addAttribute("foodId", id);
        model.addAttribute("categories", categoryService.getActiveCategories());
        return "admin/foods/form";
    }

    @PostMapping("/edit/{id}")
    public String updateFood(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("foodRequest") FoodRequest foodRequest,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("foodId", id);
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "admin/foods/form";
        }

        try {
            foodService.updateFood(id, foodRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Food item updated successfully!");
            return "redirect:/admin/foods";
        } catch (Exception e) {
            model.addAttribute("foodId", id);
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/foods/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteFood(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            foodService.deleteFood(id);
            redirectAttributes.addFlashAttribute("successMessage", "Food item deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete food item: " + e.getMessage());
        }
        return "redirect:/admin/foods";
    }

    @PostMapping("/toggle/{id}")
    public String toggleAvailability(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            foodService.toggleFoodAvailability(id);
            redirectAttributes.addFlashAttribute("successMessage", "Food availability toggled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/foods";
    }
}
