package com.foodapp.controller;

import com.foodapp.dto.CartItemRequest;
import com.foodapp.model.Cart;
import com.foodapp.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCartForUser(userId);
    }

    @PostMapping("/{userId}/items")
    public Cart addItem(@PathVariable Long userId, @RequestBody CartItemRequest request) {
        return cartService.addItem(userId, request);
    }

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public Cart removeItem(@PathVariable Long userId, @PathVariable Long cartItemId) {
        return cartService.removeItem(userId, cartItemId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
