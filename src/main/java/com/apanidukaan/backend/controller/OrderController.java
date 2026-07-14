package com.apanidukaan.backend.controller;

import com.apanidukaan.backend.model.Order;
import com.apanidukaan.backend.model.OrderStatus;
import com.apanidukaan.backend.security.FirebaseUserPrincipal;
import com.apanidukaan.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // PUBLIC — customer checkout, no login required
    @PostMapping
    public Order createOrder(@Valid @RequestBody Order order) {
        return orderService.createOrder(order);
    }

    // PUBLIC — customer tracking their own order
    @GetMapping("/track/{orderNumber}")
    public Order trackOrder(@PathVariable String orderNumber) {
        return orderService.getOrderByNumber(orderNumber);
    }

    // PROTECTED — shopkeeper viewing their orders
    @GetMapping("/shop/{shopId}")
    public List<Order> getOrdersForShop(@PathVariable String shopId) {
        return orderService.getOrdersForShop(shopId, getCurrentUid());
    }

    // PROTECTED — shopkeeper updating order status
    @PatchMapping("/{orderId}/status")
    public Order updateStatus(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));
        return orderService.updateOrderStatus(orderId, newStatus, getCurrentUid());
    }

    private String getCurrentUid() {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUid();
    }
}