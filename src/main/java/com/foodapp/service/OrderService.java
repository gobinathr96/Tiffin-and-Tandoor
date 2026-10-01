package com.foodapp.service;

import com.foodapp.dto.KitchenOrderNotification;
import com.foodapp.dto.OrderPlacedResponse;
import com.foodapp.dto.PlaceOrderRequest;
import com.foodapp.exception.ResourceNotFoundException;
import com.foodapp.model.*;
import com.foodapp.repository.OrderRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    // ---- Loyalty program rules (tweak these numbers freely) ----
    // 1 point per Rs.100 spent, capped at 10 points per order.
    private static final int MAX_POINTS_PER_ORDER = 10;
    // 100+ points on file at checkout time -> 10% off this order.
    private static final int TIER1_POINTS_REQUIRED = 100;
    private static final int TIER1_DISCOUNT_PERCENT = 10;
    // 200+ points on file at checkout time -> 20% off this order.
    private static final int TIER2_POINTS_REQUIRED = 200;
    private static final int TIER2_DISCOUNT_PERCENT = 20;

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final BillService billService;
    private final BillMailService billMailService;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderService(OrderRepository orderRepository,
                         CartService cartService,
                         UserService userService,
                         BillService billService,
                         BillMailService billMailService,
                         SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.billService = billService;
        this.billMailService = billMailService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public OrderPlacedResponse placeOrder(PlaceOrderRequest request) {
        Cart cart = cartService.getCartForUser(request.getUserId());
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart");
        }

        User user = userService.getById(request.getUserId());

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setTableNumber(request.getTableNumber());
        order.setStatus(Order.OrderStatus.PLACED);

        double subtotal = 0;
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFoodItem(cartItem.getFoodItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtOrderTime(cartItem.getFoodItem().getPrice());
            order.getItems().add(orderItem);
            subtotal += cartItem.getFoodItem().getPrice() * cartItem.getQuantity();
        }

        // ---- Apply loyalty discount based on points already on file ----
        int pointsOnFile = user.getLoyaltyPoints();
        int discountPercent = 0;
        int pointsRedeemed = 0;
        if (pointsOnFile >= TIER2_POINTS_REQUIRED) {
            discountPercent = TIER2_DISCOUNT_PERCENT;
            pointsRedeemed = TIER2_POINTS_REQUIRED;
        } else if (pointsOnFile >= TIER1_POINTS_REQUIRED) {
            discountPercent = TIER1_DISCOUNT_PERCENT;
            pointsRedeemed = TIER1_POINTS_REQUIRED;
        }
        double finalTotal = subtotal * (1 - discountPercent / 100.0);

        // ---- Earn new points on what was actually paid ----
        int pointsEarned = Math.max(1, Math.min(MAX_POINTS_PER_ORDER, (int) (finalTotal / 100)));

        order.setTotalAmount(finalTotal);
        order.setDiscountPercent(discountPercent);
        order.setPointsEarned(pointsEarned);
        order.setPointsRedeemed(pointsRedeemed);

        Order saved = orderRepository.save(order);

        // ---- Update the customer's running point balance ----
        int newBalance = pointsOnFile - pointsRedeemed + pointsEarned;
        user.setLoyaltyPoints(newBalance);
        userService.save(user);

        // 1) Auto-generate the bill the instant the order is saved
        String bill = billService.generateBillText(saved);

        // 2) Email that bill to the restaurant's order-desk mailbox
        billMailService.sendBillEmail(saved, bill);

        // 3) Push a live notification to any connected kitchen/staff dashboard
        notifyKitchen(saved, user);

        // Empty the cart now that the order has been placed
        cartService.clearCart(request.getUserId());

        return new OrderPlacedResponse(saved, bill, newBalance);
    }

    private void notifyKitchen(Order saved, User user) {
        List<String> itemSummaries = new ArrayList<>();
        for (OrderItem oi : saved.getItems()) {
            itemSummaries.add(oi.getFoodItem().getName() + " x " + oi.getQuantity());
        }

        KitchenOrderNotification notification = new KitchenOrderNotification();
        notification.setOrderId(saved.getId());
        notification.setCustomerName(user.getName());
        notification.setDeliveryAddress(saved.getDeliveryAddress());
        notification.setTotalAmount(saved.getTotalAmount());
        notification.setItems(itemSummaries);
        notification.setPlacedAt(saved.getCreatedAt());

        // Anyone subscribed to /topic/kitchen-orders gets this instantly
        messagingTemplate.convertAndSend("/topic/kitchen-orders", notification);
    }

    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
    }

    public Order updateStatus(Long id, Order.OrderStatus status) {
        Order order = getById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
