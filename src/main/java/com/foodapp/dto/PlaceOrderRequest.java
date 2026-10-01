package com.foodapp.dto;

public class PlaceOrderRequest {
    private Long userId;
    private String deliveryAddress;
    private String tableNumber; // optional - set for dine-in orders at a booked table

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }
}
