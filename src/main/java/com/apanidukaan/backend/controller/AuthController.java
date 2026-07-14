
package com.apanidukaan.backend.controller;

import com.apanidukaan.backend.model.User;
import com.apanidukaan.backend.security.FirebaseUserPrincipal;
import com.apanidukaan.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // call this right after a successful frontend login
    @PostMapping("/sync")
    public User syncUser() {
        FirebaseUserPrincipal principal = getCurrentPrincipal();
        return authService.syncUser(principal.getUid(), principal.getEmail());
    }

    @GetMapping("/me")
    public User getProfile() {
        FirebaseUserPrincipal principal = getCurrentPrincipal();
        return authService.getCurrentUserProfile(principal.getUid());
    }

    private FirebaseUserPrincipal getCurrentPrincipal() {
        return (FirebaseUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}