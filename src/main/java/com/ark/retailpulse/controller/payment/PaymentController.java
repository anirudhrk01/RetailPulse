//package com.ark.retailpulse.controller.payment;
//
//import com.ark.retailpulse.model.Order;
//import com.ark.retailpulse.repository.OrderRepository;
//import com.razorpay.RazorpayException;
//import com.razorpay.Utils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.util.Map;
//
//@RestController
////@RequestMapping("/api/payment")
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class PaymentController {
//
//    private final OrderRepository orderRepository;
//    @Value("${razorpay.secret.key}")
//    private String SECRET_KEY;
//
//    // Endpoint to receive Razorpay webhook notifications
//    @PostMapping("/verify-payment")
//    public ResponseEntity<String> handlePaymentWebhook(@RequestBody Map<String, Object> webhookData) {
//        // Step 1: Verify the webhook signature
//        boolean isSignatureVerified = verifyRazorpayWebhookSignature(webhookData, SECRET_KEY);
//
//        if (!isSignatureVerified) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature.");
//        }
//        try {
//
//            String event = (String) webhookData.get("event");
//
//            // Process the order payment if the event is "order.paid"
//            if ("order.paid".equals(event)) {
//                Map<String, Object> payload = (Map<String, Object>) webhookData.get("payload");
//                Map<String, Object> payment = (Map<String, Object>) payload.get("payment");
//                Map<String, Object> paymentEntity = (Map<String, Object>) payment.get("entity");
//
//                String paymentId = (String) paymentEntity.get("id");
//                String orderId = (String) paymentEntity.get("order_id");
//
//                //  Process the order if it exists
//                Order order = orderRepository.findByRazorpayOrderId(orderId);
//                if (order != null) {
//                    order.setPaymentId(paymentId);
//                    order.setPaymentStatus("PAID");
//                    orderRepository.save(order);
//                }
//            }
//
//            return ResponseEntity.ok("Webhook processed successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed: " + e.getMessage());
//        }
//    }
//
//    // Method to verify Razorpay webhook signature
//    private boolean verifyRazorpayWebhookSignature(Map<String, Object> webhookData, String secretKey) {
//        String webhookSignature = (String) webhookData.get("signature");
//
//        // Using ObjectMapper to serialize the webhookData map into a JSON string
//        ObjectMapper objectMapper = new ObjectMapper();
//        String payload;
//
//        try {
//            // Convert the map to JSON string
//            payload = objectMapper.writeValueAsString(webhookData);
//        } catch (Exception e) {
//            // Handle exception if serialization fails
//            return false;
//        }
//
//        try {
//            // Verify the webhook signature using Razorpay's Utils method
//            return Utils.verifyWebhookSignature(payload, webhookSignature, secretKey);
//        } catch (RazorpayException e) {
//            // Handle exception (invalid signature)
//            return false;
//        }
//    }
//}
