package com.foodapp.controller;

import com.foodapp.model.FoodItem;
import com.foodapp.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @GetMapping
    public List<FoodItem> getAll(@RequestParam(required = false) String category,
                                  @RequestParam(required = false) String search) {
        if (search != null) return foodItemService.search(search);
        if (category != null) return foodItemService.getByCategory(category);
        return foodItemService.getAll();
    }

    @GetMapping("/{id}")
    public FoodItem getOne(@PathVariable Long id) {
        return foodItemService.getById(id);
    }

    @GetMapping("/today-special")
    public List<FoodItem> getTodaySpecials() {
        return foodItemService.getTodaySpecials();
    }

    // --- Admin-only endpoints (menu management) ---

    @PostMapping
    public ResponseEntity<FoodItem> create(@Valid @RequestBody FoodItem item) {
        return ResponseEntity.ok(foodItemService.create(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItem> update(@PathVariable Long id, @Valid @RequestBody FoodItem item) {
        return ResponseEntity.ok(foodItemService.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        foodItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
