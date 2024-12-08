package com.ark.retailpulse.service.order;

import com.ark.retailpulse.dto.cart.CartDTO;
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
    private final OtpRepository otpRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public OrderDTO createOrder(Long userId, String address, String phoneNumber) throws RazorpayException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if OTP is verified for the user
        if (!user.isOtpVerified()) {
            throw new IllegalStateException("OTP not verified for the user. Please verify OTP first.");
        }


        CartDtoDetails cartDTO = cartService.getCart(userId);


        if (cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create an order with an empty cart");
        }

        Cart cart = cartMapper.toCart(cartDTO);

        // Logging cart items for debugging
        cart.getItems().forEach(item -> {
            logger.info("CartItem: {} - Product: {} - Price: {}", item, item.getProduct(),
                    item.getProduct() != null ? item.getProduct().getPrice() : "null");
        });
        // validation for cart items
        cart.getItems().forEach(item -> {
            if (item.getProduct() == null || item.getProduct().getPrice() == null) {
                throw new IllegalStateException("Invalid cart item: Product or price is null");
            }
        });

        BigDecimal totalAmount = cart.getItems().stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getPrice() != null)
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        com.razorpay.Order razorpayOrder = paymentService.createRazorpayOrder(totalAmount, "order_receipt_" + System.currentTimeMillis());

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

        try {
            emailService.sendOrderConfirmation(savedOrder);
        } catch (MailException e) {
            logger.error("Failed to send order confirmation for order ID {} to user {}", savedOrder.getId(), savedOrder.getUser().getEmail(), e);
        }
        Cart cart1=cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        cart1.getItems().clear();
        cartRepository.save(cart1);
       // cartItemRepository.deleteByCartId(cart1.getId());
        return orderMapper.toDTO(order);
    }

    public List<OrderDTO> getAllOrders() {
        return orderMapper.toDTOs(orderRepository.findAll());
    }

    public List<OrderDTO> getUserOrders(Long userId) {
        return orderMapper.toDTOs(orderRepository.findByUserId(userId));
    }

    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        order.setStatus(orderStatus);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        return paymentService.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
    }


}
