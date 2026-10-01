package com.foodapp.controller;

import com.foodapp.dto.OrderPlacedResponse;
import com.foodapp.dto.PlaceOrderRequest;
import com.foodapp.model.Order;
import com.foodapp.service.BillService;
import com.foodapp.service.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final BillService billService;

    public OrderController(OrderService orderService, BillService billService) {
        this.orderService = orderService;
        this.billService = billService;
    }

    @PostMapping
    public OrderPlacedResponse placeOrder(@RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(request);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersForUser(@PathVariable Long userId) {
        return orderService.getOrdersForUser(userId);
    }

    @GetMapping("/{id}")
    public Order getOne(@PathVariable Long id) {
        return orderService.getById(id);
    }

    // Re-download the bill for any past order (e.g. from order history)
    @GetMapping("/{id}/bill")
    public ResponseEntity<String> downloadBill(@PathVariable Long id) {
        Order order = orderService.getById(id);
        String bill = billService.generateBillText(order);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(bill);
    }

    // --- Admin-only: update order status as it moves through the kitchen ---
    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        return orderService.updateStatus(id, status);
    }
}
