package com.foodapp.repository;

import com.foodapp.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByCategory(String category);
    List<FoodItem> findByAvailableTrue();
    List<FoodItem> findByNameContainingIgnoreCase(String name);
    List<FoodItem> findByTodaySpecialTrueAndAvailableTrue();
}
