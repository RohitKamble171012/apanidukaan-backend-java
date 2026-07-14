package com.apanidukaan.backend.controller;

import com.apanidukaan.backend.service.PaymentService;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // PUBLIC — called right after order creation, before payment
    @PostMapping("/create-order/{orderId}")
    public ResponseEntity<?> createRazorpayOrder(@PathVariable String orderId) {
        try {
            JSONObject result = paymentService.createRazorpayOrder(orderId);
            return ResponseEntity.ok(result.toString());
        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUBLIC — frontend calls this right after Razorpay's checkout widget succeeds
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> body) {
        String orderId = body.get("razorpay_order_id");
        String paymentId = body.get("razorpay_payment_id");
        String signature = body.get("razorpay_signature");

        boolean valid = paymentService.verifyPaymentSignature(orderId, paymentId, signature);

        if (!valid) {
            return ResponseEntity.badRequest().body(Map.of("verified", false, "message", "Invalid signature"));
        }

        paymentService.markOrderPaid(orderId, paymentId);
        return ResponseEntity.ok(Map.of("verified", true));
    }

    // PUBLIC — called by Razorpay's servers directly, not your frontend
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        boolean valid = paymentService.verifyWebhookSignature(payload, signature);

        if (!valid) {
            return ResponseEntity.status(400).body("Invalid webhook signature");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        if ("payment.captured".equals(eventType)) {
            JSONObject paymentEntity = event.getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");
            String razorpayOrderId = paymentEntity.getString("order_id");
            String razorpayPaymentId = paymentEntity.getString("id");
            paymentService.markOrderPaid(razorpayOrderId, razorpayPaymentId);
        }

        return ResponseEntity.ok("OK");
    }

    private String getCurrentUid() {
        return null; // not needed here, kept for consistency with other controllers if you extend this later
    }
}