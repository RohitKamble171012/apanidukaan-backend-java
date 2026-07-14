package com.apanidukaan.backend.repository;

import com.apanidukaan.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUid(String uid);
}