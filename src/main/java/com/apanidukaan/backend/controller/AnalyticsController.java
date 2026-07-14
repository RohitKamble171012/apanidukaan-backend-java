package com.apanidukaan.backend.controller;

import com.apanidukaan.backend.security.FirebaseUserPrincipal;
import com.apanidukaan.backend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/shop/{shopId}")
    public Map<String, Object> getStats(@PathVariable String shopId) {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return analyticsService.getDashboardStats(shopId, principal.getUid());
    }
}