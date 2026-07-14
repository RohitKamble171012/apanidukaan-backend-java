package com.apanidukaan.backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "shops")
public class Shop {

    @Id
    private String id;

    @NotBlank(message = "slug is required")
    @Indexed(unique = true)
    private String slug;

    @NotBlank(message = "ownerUid is required")
    private String ownerUid;

    @NotBlank(message = "shopName is required")
    private String shopName;

    @NotBlank(message = "category is required")
    private String category;

    private String phone;
    private String address;
    private boolean visible = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}