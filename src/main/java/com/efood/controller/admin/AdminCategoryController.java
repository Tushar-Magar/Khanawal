package com.efood.controller.admin;

import com.efood.dto.CategoryRequest;
import com.efood.entity.Category;
import com.efood.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        Map<Long, Long> foodCounts = new HashMap<>();
        for (Category cat : categories) {
            foodCounts.put(cat.getId(), categoryService.countFoodsInCategory(cat.getId()));
        }
        model.addAttribute("categories", categories);
        model.addAttribute("foodCounts", foodCounts);
        return "admin/categories/list";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        if (!model.containsAttribute("categoryRequest")) {
            model.addAttribute("categoryRequest", new CategoryRequest());
        }
        return "admin/categories/form";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("categoryRequest") CategoryRequest categoryRequest,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/categories/form";
        }

        try {
            categoryService.saveCategory(categoryRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Category created successfully!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/categories/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id);
        CategoryRequest categoryRequest = new CategoryRequest(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl(),
                category.getActive()
        );
        model.addAttribute("categoryRequest", categoryRequest);
        model.addAttribute("categoryId", id);
        return "admin/categories/form";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("categoryRequest") CategoryRequest categoryRequest,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryId", id);
            return "admin/categories/form";
        }

        try {
            categoryService.updateCategory(id, categoryRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            model.addAttribute("categoryId", id);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/categories/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCategoryStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.toggleCategoryStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
