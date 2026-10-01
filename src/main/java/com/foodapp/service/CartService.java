package com.foodapp.service;

import com.foodapp.dto.CartItemRequest;
import com.foodapp.exception.ResourceNotFoundException;
import com.foodapp.model.Cart;
import com.foodapp.model.CartItem;
import com.foodapp.model.FoodItem;
import com.foodapp.model.User;
import com.foodapp.repository.CartItemRepository;
import com.foodapp.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final FoodItemService foodItemService;

    public CartService(CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        UserService userService,
                        FoodItemService foodItemService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.foodItemService = foodItemService;
    }

    public Cart getCartForUser(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userService.getById(userId);
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    public Cart addItem(Long userId, CartItemRequest request) {
        Cart cart = getCartForUser(userId);
        FoodItem foodItem = foodItemService.getById(request.getFoodItemId());

        // If the item is already in the cart, just increase the quantity
        cart.getItems().stream()
                .filter(i -> i.getFoodItem().getId().equals(foodItem.getId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + request.getQuantity()),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setCart(cart);
                            newItem.setFoodItem(foodItem);
                            newItem.setQuantity(request.getQuantity());
                            cart.getItems().add(newItem);
                        }
                );
        return cartRepository.save(cart);
    }

    public Cart removeItem(Long userId, Long cartItemId) {
        Cart cart = getCartForUser(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id " + cartItemId));
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return cartRepository.save(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = getCartForUser(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
