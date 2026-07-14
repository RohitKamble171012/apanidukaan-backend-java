package com.apanidukaan.backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "feedback")
public class Feedback {

    @Id
    private String id;

    @NotBlank(message = "shopId is required")
    @Indexed
    private String shopId;

    @NotBlank(message = "customerName is required")
    private String customerName;

    private String customerPhone;

    @NotBlank(message = "message is required")
    private String message;

    // optional — customers can request an item the shop doesn't currently stock
    private String itemRequested;

    private Integer rating; // 1-5, optional

    private boolean resolved = false;

    @CreatedDate
    private Instant createdAt;
}