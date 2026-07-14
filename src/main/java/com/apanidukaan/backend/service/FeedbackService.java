 package com.apanidukaan.backend.service;

import com.apanidukaan.backend.exception.ForbiddenException;
import com.apanidukaan.backend.exception.ResourceNotFoundException;
import com.apanidukaan.backend.model.Feedback;
import com.apanidukaan.backend.model.Shop;
import com.apanidukaan.backend.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ShopService shopService;

    public Feedback createFeedback(Feedback feedback) {
        // confirms the shop actually exists before accepting feedback for it
        shopService.getShopById(feedback.getShopId());
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackForShop(String shopId, String currentUid) {
        Shop shop = shopService.getShopById(shopId);
        if (!shop.getOwnerUid().equals(currentUid)) {
            throw new ForbiddenException("You do not own this shop.");
        }
        return feedbackRepository.findByShopId(shopId);
    }

    public Feedback markResolved(String feedbackId, String currentUid) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found: " + feedbackId));

        Shop shop = shopService.getShopById(feedback.getShopId());
        if (!shop.getOwnerUid().equals(currentUid)) {
            throw new ForbiddenException("You do not own this shop.");
        }

        feedback.setResolved(true);
        return feedbackRepository.save(feedback);
    }
}