package com.efood.controller;

import com.efood.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getActiveCategories());
        return "category/list";
    }

    @GetMapping("/categories/{id}/foods")
    public String foodsByCategory(@PathVariable("id") Long id) {
        return "redirect:/foods?category=" + id;
    }
}
