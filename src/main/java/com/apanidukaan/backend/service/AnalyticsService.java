package com.apanidukaan.backend.service;

import com.apanidukaan.backend.model.Order;
import com.apanidukaan.backend.model.OrderItem;
import com.apanidukaan.backend.model.OrderStatus;
import com.apanidukaan.backend.model.Shop;
import com.apanidukaan.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShopService shopService;

    public Map<String, Object> getDashboardStats(String shopId, String currentUid) {
        Shop shop = shopService.getShopById(shopId);
        if (!shop.getOwnerUid().equals(currentUid)) {
            throw new RuntimeException("You do not own this shop.");
        }

        List<Order> orders = orderRepository.findByShopId(shopId);

        long totalOrders = orders.size();

        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .count();

        Map<String, Integer> productSales = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                productSales.merge(item.getProductName(), item.getQuantity(), Integer::sum);
            }
        }

        List<Map.Entry<String, Integer>> topProducts = productSales.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .collect(Collectors.toList());

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("pendingOrders", pendingOrders);
        stats.put("topProducts", topProducts);

        return stats;
    }
}