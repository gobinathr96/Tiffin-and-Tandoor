package com.foodapp.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Small, flat summary of a new order — this is what gets pushed
 * over WebSocket to /topic/kitchen-orders the instant an order is placed.
 */
public class KitchenOrderNotification {

    private Long orderId;
    private String customerName;
    private String deliveryAddress;
    private double totalAmount;
    private List<String> items; // e.g. "Butter Chicken x 2"
    private LocalDateTime placedAt;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public List<String> getItems() { return items; }
    public void setItems(List<String> items) { this.items = items; }

    public LocalDateTime getPlacedAt() { return placedAt; }
    public void setPlacedAt(LocalDateTime placedAt) { this.placedAt = placedAt; }
}
