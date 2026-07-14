package com.apanidukaan.backend.service;

import com.apanidukaan.backend.exception.ForbiddenException;
import com.apanidukaan.backend.exception.ResourceNotFoundException;
import com.apanidukaan.backend.model.*;
import com.apanidukaan.backend.repository.OrderRepository;
import com.apanidukaan.backend.repository.ProductRepository;
import com.apanidukaan.backend.util.OrderNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopService shopService;

    @Autowired
    private OrderNumberGenerator orderNumberGenerator;

    public Order createOrder(Order order) {
        // 1. confirm the shop exists
        Shop shop = shopService.getShopById(order.getShopId());

        // 2. validate every item against real product data, compute total
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: " + item.getProductId()));

            if (!product.getShopId().equals(shop.getId())) {
                throw new IllegalArgumentException(
                        "Product " + product.getProductName() + " does not belong to this shop");
            }

            if (!product.isAvailability() || product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for: " + product.getProductName());
            }

            // trust the real product price, not whatever the client sent
            item.setPrice(product.getPrice());
            item.setProductName(product.getProductName());

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            // 3. deduct stock
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        order.setOrderNumber(orderNumberGenerator.generate());
        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
    }

    public List<Order> getOrdersForShop(String shopId, String currentUid) {
        Shop shop = shopService.getShopById(shopId);
        if (!shop.getOwnerUid().equals(currentUid)) {
            throw new ForbiddenException("You do not own this shop.");
        }
        return orderRepository.findByShopId(shopId);
    }

    public Order updateOrderStatus(String orderId, OrderStatus newStatus, String currentUid) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        Shop shop = shopService.getShopById(order.getShopId());
        if (!shop.getOwnerUid().equals(currentUid)) {
            throw new ForbiddenException("You do not own this shop.");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}