package com.apanidukaan.backend.service;

import com.apanidukaan.backend.model.User;
import com.apanidukaan.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User syncUser(String uid, String email) {
        return userRepository.findByUid(uid)
                .orElseGet(() -> {
                    User user = new User();
                    user.setUid(uid);
                    user.setEmail(email);
                    return userRepository.save(user);
                });
    }

    public User getCurrentUserProfile(String uid) {
        return userRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found: " + uid));
    }
}