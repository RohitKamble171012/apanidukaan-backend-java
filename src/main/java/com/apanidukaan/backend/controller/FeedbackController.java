package com.apanidukaan.backend.controller;

import com.apanidukaan.backend.model.Feedback;
import com.apanidukaan.backend.security.FirebaseUserPrincipal;
import com.apanidukaan.backend.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // PUBLIC — any customer can submit feedback, no login required
    @PostMapping
    public Feedback createFeedback(@Valid @RequestBody Feedback feedback) {
        return feedbackService.createFeedback(feedback);
    }

    // PROTECTED — shopkeeper viewing their own feedback
    @GetMapping("/shop/{shopId}")
    public List<Feedback> getFeedbackForShop(@PathVariable String shopId) {
        return feedbackService.getFeedbackForShop(shopId, getCurrentUid());
    }

    // PROTECTED — shopkeeper marking feedback as addressed
    @PatchMapping("/{feedbackId}/resolve")
    public Feedback resolve(@PathVariable String feedbackId) {
        return feedbackService.markResolved(feedbackId, getCurrentUid());
    }

    private String getCurrentUid() {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUid();
    }
}