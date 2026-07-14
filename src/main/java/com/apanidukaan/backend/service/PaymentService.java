package com.apanidukaan.backend.service;

import com.apanidukaan.backend.exception.ResourceNotFoundException;
import com.apanidukaan.backend.model.Order;
import com.apanidukaan.backend.repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    private final OrderRepository orderRepository;

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public JSONObject createRazorpayOrder(String orderId) throws RazorpayException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        int amountInPaise = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", order.getOrderNumber());

        com.razorpay.Order razorpayOrder = client.orders.create(orderRequest);

        String razorpayOrderId = razorpayOrder.get("id");

        order.setRazorpayOrderId(razorpayOrderId);
        orderRepository.save(order);

        JSONObject response = new JSONObject();
        response.put("razorpayOrderId", razorpayOrderId);
        response.put("amount", amountInPaise);
        response.put("currency", "INR");
        response.put("keyId", keyId);

        return response;
    }

    public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (RazorpayException e) {
            return false;
        }
    }

    public void markOrderPaid(String razorpayOrderId, String razorpayPaymentId) {
        Order order = orderRepository.findAll().stream()
                .filter(o -> razorpayOrderId.equals(o.getRazorpayOrderId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No order found for Razorpay order: " + razorpayOrderId));

        order.setRazorpayPaymentId(razorpayPaymentId);
        order.setPaymentStatus("PAID");
        orderRepository.save(order);
    }

    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            return Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            return false;
        }
    }
}