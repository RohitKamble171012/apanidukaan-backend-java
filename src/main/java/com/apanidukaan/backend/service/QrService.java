package com.apanidukaan.backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QrService {

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public byte[] generateShopQr(String slug) throws WriterException, IOException {
        String shopUrl = frontendBaseUrl + "/shop/" + slug;

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(shopUrl, BarcodeFormat.QR_CODE, 400, 400);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        }
    }
}