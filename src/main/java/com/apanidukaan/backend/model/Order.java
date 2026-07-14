package com.apanidukaan.backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @Indexed(unique = true)
    private String orderNumber;

    @NotBlank(message = "shopId is required")
    @Indexed
    private String shopId;

    @NotBlank(message = "customerName is required")
    private String customerName;

    @NotBlank(message = "customerPhone is required")
    private String customerPhone;

    private String deliveryAddress;

    @NotEmpty(message = "order must contain at least one item")
    private List<OrderItem> items;

    private BigDecimal totalAmount;

    private OrderStatus status = OrderStatus.PENDING;

    // payment fields
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String paymentStatus = "PENDING"; // PENDING, PAID, FAILED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}