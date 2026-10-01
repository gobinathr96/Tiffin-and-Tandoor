package com.foodapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "food_items")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Food name is required")
    private String name;

    private String description;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    private String category; // e.g. Starters, Main Course, Desserts, Beverages

    private String imageUrl;

    private boolean available = true;

    // Marked true for dishes featured on the "Today's Special" page
    private boolean todaySpecial = false;

    // ---- Getters and setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isTodaySpecial() { return todaySpecial; }
    public void setTodaySpecial(boolean todaySpecial) { this.todaySpecial = todaySpecial; }
}
