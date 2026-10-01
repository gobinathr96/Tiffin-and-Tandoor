package com.foodapp.dto;

import com.foodapp.model.Order;

/**
 * What POST /api/orders returns: the saved order, plus the
 * auto-generated bill text ready to show or download immediately.
 */
public class OrderPlacedResponse {

    private Order order;
    private String bill;
    private int loyaltyPointsBalance;

    public OrderPlacedResponse(Order order, String bill, int loyaltyPointsBalance) {
        this.order = order;
        this.bill = bill;
        this.loyaltyPointsBalance = loyaltyPointsBalance;
    }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getBill() { return bill; }
    public void setBill(String bill) { this.bill = bill; }

    public int getLoyaltyPointsBalance() { return loyaltyPointsBalance; }
    public void setLoyaltyPointsBalance(int loyaltyPointsBalance) { this.loyaltyPointsBalance = loyaltyPointsBalance; }
}
