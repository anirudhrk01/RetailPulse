package com.ark.retailpulse.controller.payment;

import com.ark.retailpulse.model.Order;
import com.ark.retailpulse.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VerifyController {

    private static final Logger logger = LoggerFactory.getLogger(VerifyController.class);

    private final OrderRepository orderRepository;

    /**
     * Endpoint to handle Razorpay payment webhook events.
     * This method processes payment-related events, updates the corresponding order,
     * and returns an appropriate response.
     *
     * @param webhookData  The webhook payload from Razorpay, provided as a Map.
     * @return A ResponseEntity indicating the result of webhook processing.
     */
    @PostMapping("/verify-payment")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody Map<String, Object> webhookData) {
        logger.info("Received webhook data: {}", webhookData);

        try {
            String event = (String) webhookData.get("event");

            // Log the type of event received
            logger.info("Processing webhook event: {}", event);

            if ("order.paid".equals(event)) {
                // Extract nested payload details
                Map<String, Object> payload = (Map<String, Object>) webhookData.get("payload");
                Map<String, Object> payment = (Map<String, Object>) payload.get("payment");
                Map<String, Object> paymentEntity = (Map<String, Object>) payment.get("entity");

                // Extract payment and order IDs from the payload
                String paymentId = (String) paymentEntity.get("id");
                String orderId = (String) paymentEntity.get("order_id");

                logger.info("Processing payment for order ID: {}, payment ID: {}", orderId, paymentId);

                // Fetch the corresponding order from the database
                Order order = orderRepository.findByRazorpayOrderId(orderId);
                if (order != null) {
                    // Update the order's payment details and status
                    order.setPaymentId(paymentId);
                    order.setPaymentStatus("PAID");
                    orderRepository.save(order);

                    logger.info("Order ID: {} updated with payment ID: {} and status: PAID", orderId, paymentId);
                } else {
                    logger.warn("Order with Razorpay Order ID: {} not found", orderId);
                }
            }

            // Successfully processed the webhook
            logger.info("Webhook processing completed successfully.");
            return ResponseEntity.ok("Webhook processed");
        } catch (Exception e) {
            // Log the exception and return an error response
            logger.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }
}
