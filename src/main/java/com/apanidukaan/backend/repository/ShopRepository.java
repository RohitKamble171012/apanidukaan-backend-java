package com.apanidukaan.backend.repository;

import com.apanidukaan.backend.model.Shop;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShopRepository extends MongoRepository<Shop, String> {
    Optional<Shop> findBySlug(String slug);
    Optional<Shop> findByOwnerUid(String ownerUid);
}