package com.ark.retailpulse.controller.order;

import com.ark.retailpulse.dto.order.OrderDTO;
import com.ark.retailpulse.model.Order;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.service.order.OrderService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling order-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    /**
     * Creates a new order for the authenticated user.
     *
     * @param userDetails The details of the authenticated user.
     * @param address     The delivery address for the order.
     * @param phoneNumber The contact number for the order.
     * @return The created order details.
     * @throws RazorpayException If an error occurs during payment initiation.
     */
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestParam String address,
                                                @RequestParam String phoneNumber) throws RazorpayException {
        Long userId = ((User) userDetails).getId();
        logger.info("Creating order for userId: {}, address: {}, phoneNumber: {}", userId, address, phoneNumber);
        OrderDTO orderDTO = orderService.createOrder(userId, address, phoneNumber);
        logger.info("Order created successfully for userId: {}", userId);
        return ResponseEntity.ok(orderDTO);
    }

    /**
     * Retrieves all orders (accessible by admin users).
     *
     * @return A list of all orders.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        logger.info("Fetching all orders");
        List<OrderDTO> orders = orderService.getAllOrders();
        logger.info("Total orders retrieved: {}", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves orders specific to the authenticated user.
     *
     * @param userDetails The details of the authenticated user.
     * @return A list of orders made by the user.
     */
    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((User) userDetails).getId();
        logger.info("Fetching orders for userId: {}", userId);
        List<OrderDTO> orders = orderService.getUserOrders(userId);
        logger.info("Orders retrieved for userId {}: {}", userId, orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * Updates the status of an order (accessible by admin users).
     *
     * @param orderId The ID of the order to be updated.
     * @param status  The new status of the order.
     * @return The updated order details.
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId,
                                                      @RequestParam Order.OrderStatus status) {
        logger.info("Updating order status for orderId: {}, new status: {}", orderId, status);
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        logger.info("Order status updated successfully for orderId: {}", orderId);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Verifies a payment for an order.
     *
     * @param razorpayOrderId   The Razorpay order ID.
     * @param razorpayPaymentId The Razorpay payment ID.
     * @param razorpaySignature The Razorpay payment signature.
     * @return A success or failure response for payment verification.
     */
    @PostMapping("/verify-payment")
    public ResponseEntity<String> verifyPayment(@RequestParam String razorpayOrderId,
                                                @RequestParam String razorpayPaymentId,
                                                @RequestParam String razorpaySignature) {
        logger.info("Verifying payment for orderId: {}", razorpayOrderId);
        boolean isVerified = orderService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);

        if (isVerified) {
            logger.info("Payment verified successfully for orderId: {}", razorpayOrderId);
            return ResponseEntity.ok("Payment verified successfully");
        } else {
            logger.warn("Payment verification failed for orderId: {}", razorpayOrderId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");
        }
    }
}
