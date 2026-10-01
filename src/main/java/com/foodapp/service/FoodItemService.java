package com.foodapp.service;

import com.foodapp.exception.ResourceNotFoundException;
import com.foodapp.model.FoodItem;
import com.foodapp.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public List<FoodItem> getAll() {
        return foodItemRepository.findByAvailableTrue();
    }

    public List<FoodItem> getByCategory(String category) {
        return foodItemRepository.findByCategory(category);
    }

    public List<FoodItem> search(String keyword) {
        return foodItemRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<FoodItem> getTodaySpecials() {
        return foodItemRepository.findByTodaySpecialTrueAndAvailableTrue();
    }

    public FoodItem getById(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id " + id));
    }

    public FoodItem create(FoodItem item) {
        return foodItemRepository.save(item);
    }

    public FoodItem update(Long id, FoodItem updated) {
        FoodItem existing = getById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setImageUrl(updated.getImageUrl());
        existing.setAvailable(updated.isAvailable());
        existing.setTodaySpecial(updated.isTodaySpecial());
        return foodItemRepository.save(existing);
    }

    public void delete(Long id) {
        foodItemRepository.delete(getById(id));
    }
}
