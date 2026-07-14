package com.apanidukaan.backend.controller;

import com.apanidukaan.backend.model.Shop;
import com.apanidukaan.backend.security.FirebaseUserPrincipal;
import com.apanidukaan.backend.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @PostMapping
    public Shop createShop(@Valid @RequestBody Shop shop) {
        return shopService.createShop(shop);
    }

    @GetMapping("/{id}")
    public Shop getShop(@PathVariable String id) {
        return shopService.getShopById(id);
    }

    @GetMapping("/slug/{slug}")
    public Shop getShopBySlug(@PathVariable String slug) {
        return shopService.getShopBySlug(slug);
    }

    @PutMapping("/{id}")
    public Shop updateShop(@PathVariable String id, @Valid @RequestBody Shop shop) {
        return shopService.updateShop(id, shop, getCurrentUid());
    }

    @DeleteMapping("/{id}")
    public void deleteShop(@PathVariable String id) {
        shopService.deleteShop(id, getCurrentUid());
    }

    private String getCurrentUid() {
        FirebaseUserPrincipal principal = (FirebaseUserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUid();
    }
}