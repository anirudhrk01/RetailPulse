package com.ark.retailpulse.service.order;

import com.ark.retailpulse.dto.order.OrderDTO;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.helper.order.OrderItemHelper;
import com.ark.retailpulse.mapper.CartMapper;
import com.ark.retailpulse.mapper.OrderMapper;
import com.ark.retailpulse.model.*;
import com.ark.retailpulse.repository.*;
import com.ark.retailpulse.response.CartDtoDetails.CartDtoDetails;
import com.ark.retailpulse.service.cart.CartService;
import com.ark.retailpulse.service.emaiil.EmailService;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final PaymentService paymentService;
    private final OrderItemHelper orderItemHelper;
    private final CartRepository cartRepository;

    /**
     * Creates a new order for the given user.
     *
     * @param userId      ID of the user placing the order.
     * @param address     Delivery address for the order.
     * @param phoneNumber Contact number for the order.
     * @return OrderDTO containing the created order details.
     * @throws RazorpayException If there is an error creating the Razorpay order.
     */
    @Transactional
    public OrderDTO createOrder(Long userId, String address, String phoneNumber) throws RazorpayException {
        logger.info("Starting order creation for user ID: {}", userId);

        // Fetch and validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        logger.info("User fetched: {} - OTP verified: {}", user.getEmail(), user.isOtpVerified());

        if (!user.isOtpVerified()) {
            logger.error("OTP not verified for user ID: {}", userId);
            throw new IllegalStateException("OTP not verified for the user. Please verify OTP first.");
        }

        // Fetch and validate cart
        CartDtoDetails cartDTO = cartService.getCart(userId);
        if (cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
            logger.error("Empty cart for user ID: {}", userId);
            throw new IllegalStateException("Cannot create an order with an empty cart");
        }
        Cart cart = cartMapper.toCart(cartDTO);
        logger.info("Cart fetched and mapped for user ID: {} with {} items", userId, cart.getItems().size());

        // Validate cart items
        cart.getItems().forEach(item -> {
            if (item.getProduct() == null || item.getProduct().getPrice() == null) {
                logger.error("Invalid cart item: {}", item);
                throw new IllegalStateException("Invalid cart item: Product or price is null");
            }
        });

        // Calculate total order amount
        BigDecimal totalAmount = cart.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getPrice() != null)
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("Total order amount calculated for user ID {}: {}", userId, totalAmount);

        // Create Razorpay order
        com.razorpay.Order razorpayOrder = paymentService.createRazorpayOrder(totalAmount, "order_receipt_" + System.currentTimeMillis());
        logger.info("Razorpay order created with ID: ");

        // Create and save order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPhoneNumber(phoneNumber);
        order.setStatus(Order.OrderStatus.PREPARING);
        order.setCreatedAt(LocalDateTime.now());
        order.setRazorpayOrderId(razorpayOrder.get("id"));
        order.setAmount(totalAmount);

        List<OrderItem> orderItems = orderItemHelper.createOrderItems(cart, order);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved for user ID: {} with Order ID: {}", userId, savedOrder.getId());

        // Send order confirmation email
        try {
            emailService.sendOrderConfirmation(savedOrder);
            logger.info("Order confirmation email sent for order ID: {}", savedOrder.getId());
        } catch (MailException e) {
            logger.error("Failed to send order confirmation email for order ID: {}", savedOrder.getId(), e);
        }

        // Clear user's cart
        Cart userCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
        userCart.getItems().clear();
        cartRepository.save(userCart);
        logger.info("Cart cleared for user ID: {}", userId);

        return orderMapper.toDTO(order);
    }

    /**
     * Fetches all orders.
     *
     * @return List of all OrderDTOs.
     */
    public List<OrderDTO> getAllOrders() {
        logger.info("Fetching all orders");
        return orderMapper.toDTOs(orderRepository.findAll());
    }

    /**
     * Fetches orders for a specific user.
     *
     * @param userId ID of the user.
     * @return List of OrderDTOs for the user.
     */
    public List<OrderDTO> getUserOrders(Long userId) {
        logger.info("Fetching orders for user ID: {}", userId);
        return orderMapper.toDTOs(orderRepository.findByUserId(userId));
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId     ID of the order.
     * @param orderStatus New status for the order.
     * @return Updated OrderDTO.
     */
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus orderStatus) {
        logger.info("Updating status for order ID: {} to {}", orderId, orderStatus);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        order.setStatus(orderStatus);
        logger.info("Order status updated for order ID: {}", orderId);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    /**
     * Verifies the payment using Razorpay details.
     *
     * @param razorpayOrderId   Razorpay order ID.
     * @param razorpayPaymentId Razorpay payment ID.
     * @param razorpaySignature Razorpay signature.
     * @return true if payment verification is successful, false otherwise.
     */
    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        logger.info("Verifying payment for Razorpay Order ID: {}", razorpayOrderId);
        return paymentService.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
    }
}
