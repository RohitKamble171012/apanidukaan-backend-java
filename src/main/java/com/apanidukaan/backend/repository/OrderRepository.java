package com.apanidukaan.backend.repository;

import com.apanidukaan.backend.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByShopId(String shopId);
    Optional<Order> findByOrderNumber(String orderNumber);
}