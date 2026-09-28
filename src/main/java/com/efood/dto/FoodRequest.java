package com.efood.dto;

import jakarta.validation.constraints.*;

public class FoodRequest {

    private Long id;

    @NotBlank(message = "Food name is required")
    @Size(min = 2, max = 150, message = "Food name must be between 2 and 150 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.0", message = "Price must be greater than zero")
    private Double price;

    @NotNull(message = "Please select a category")
    private Long categoryId;

    private String imageUrl;

    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Double rating = 4.5;

    private Boolean available = true;

    public FoodRequest() {
    }

    public FoodRequest(Long id, String name, String description, Double price, Long categoryId, String imageUrl, Double rating, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
        this.rating = rating != null ? rating : 4.5;
        this.available = available != null ? available : true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
