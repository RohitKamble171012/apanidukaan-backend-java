package com.apanidukaan.backend.repository;

import com.apanidukaan.backend.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByShopId(String shopId);
}