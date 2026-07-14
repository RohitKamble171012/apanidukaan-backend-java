package com.apanidukaan.backend.service;

import com.apanidukaan.backend.exception.ForbiddenException;
import com.apanidukaan.backend.exception.ResourceNotFoundException;
import com.apanidukaan.backend.model.Shop;
import com.apanidukaan.backend.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    public Shop createShop(Shop shop) {
        return shopRepository.save(shop);
    }

    public Shop getShopById(String id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found: " + id));
    }

    public Shop getShopBySlug(String slug) {
        return shopRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found for slug: " + slug));
    }

    public Shop updateShop(String id, Shop updatedData, String currentUid) {
        Shop existing = getShopById(id);
        assertOwnership(existing, currentUid);

        existing.setShopName(updatedData.getShopName());
        existing.setCategory(updatedData.getCategory());
        existing.setPhone(updatedData.getPhone());
        existing.setAddress(updatedData.getAddress());
        existing.setVisible(updatedData.isVisible());

        return shopRepository.save(existing);
    }

    public void deleteShop(String id, String currentUid) {
        Shop existing = getShopById(id);
        assertOwnership(existing, currentUid);
        shopRepository.deleteById(id);
    }

    public boolean shopExists(String shopId) {
        return shopRepository.existsById(shopId);
    }

    private void assertOwnership(Shop shop, String currentUid) {
        if (!shop.getOwnerUid().equals(currentUid)) {
            throw new ForbiddenException("You do not own this shop.");
        }
    }
}