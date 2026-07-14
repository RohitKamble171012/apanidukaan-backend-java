package com.apanidukaan.backend.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    @NotBlank(message = "shopId is required")
    @Indexed
    private String shopId;

    @NotBlank(message = "productName is required")
    private String productName;

    @NotBlank(message = "category is required")
    private String category;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "quantity is required")
    @Min(value = 0, message = "quantity cannot be negative")
    private Integer quantity;

    @NotBlank(message = "unit is required")
    private String unit;

    private boolean availability = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}