package com.foodapp.service;

import com.foodapp.model.Order;
import com.foodapp.model.OrderItem;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Turns a saved Order into a formatted, printable bill.
 * Kept as plain text so it needs no extra libraries (no PDF dependency) -
 * it can be shown on screen, saved as a .txt file, or printed as-is.
 */
@Service
public class BillService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public String generateBillText(Order order) {
        double subtotal = order.getItems().stream()
                .mapToDouble(oi -> oi.getPriceAtOrderTime() * oi.getQuantity())
                .sum();
        double discountAmount = subtotal - order.getTotalAmount();

        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("       TIFFIN & TANDOOR - INVOICE\n");
        sb.append("========================================\n");
        sb.append("Bill No.  : ").append(formatBillNumber(order.getId())).append("\n");
        sb.append("Order No. : ").append(order.getId()).append("\n");
        sb.append("Date      : ").append(order.getCreatedAt().format(DATE_FORMAT)).append("\n");
        sb.append("Customer  : ").append(order.getUser().getName()).append("\n");
        sb.append("Mobile    : ").append(order.getUser().getPhone()).append("\n");
        sb.append("Deliver to: ").append(order.getDeliveryAddress()).append("\n");
        if (order.getTableNumber() != null && !order.getTableNumber().isBlank()) {
            sb.append("Table No. : ").append(order.getTableNumber()).append("\n");
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s %5s %11s%n", "Item", "Qty", "Amount"));
        sb.append("----------------------------------------\n");

        for (OrderItem item : order.getItems()) {
            String name = item.getFoodItem().getName();
            int qty = item.getQuantity();
            double lineTotal = item.getPriceAtOrderTime() * qty;
            sb.append(String.format("%-20s %5d %11.2f%n", truncate(name, 20), qty, lineTotal));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("%-26s %11.2f%n", "Subtotal (INR)", subtotal));
        if (order.getDiscountPercent() > 0) {
            String label = "Loyalty discount (" + order.getDiscountPercent() + "%)";
            sb.append(String.format("%-26s %11.2f%n", label, -discountAmount));
        }
        sb.append(String.format("%-26s %11.2f%n", "TOTAL PAID (INR)", order.getTotalAmount()));
        sb.append("----------------------------------------\n");
        sb.append("Loyalty points earned  : ").append(order.getPointsEarned()).append("\n");
        if (order.getPointsRedeemed() > 0) {
            sb.append("Loyalty points redeemed: ").append(order.getPointsRedeemed()).append("\n");
        }
        sb.append("========================================\n");
        sb.append("        Thank you for ordering!\n");
        sb.append("========================================\n");

        return sb.toString();
    }

    private String formatBillNumber(Long orderId) {
        return "BILL" + String.format("%06d", orderId);
    }

    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }
}
