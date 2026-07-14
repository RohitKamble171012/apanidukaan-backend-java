package com.apanidukaan.backend.controller;

import com.apanidukaan.backend.service.QrService;
import com.apanidukaan.backend.service.ShopService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    @Autowired
    private QrService qrService;

    @Autowired
    private ShopService shopService;

    @GetMapping(value = "/shop/{shopId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getShopQr(@PathVariable String shopId) throws WriterException, IOException {
        // confirms the shop actually exists before generating anything
        String slug = shopService.getShopById(shopId).getSlug();

        byte[] qrImage = qrService.generateShopQr(slug);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }
}