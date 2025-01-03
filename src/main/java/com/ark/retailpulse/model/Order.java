package com.ark.retailpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer's order in the system.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The user who placed the order.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String address;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    public enum OrderStatus {
        PREPARING, DELIVERING, DELIVERED, CANCELED
    }
    private LocalDateTime createdAt;
    /**
     * List of items associated with the order.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private String razorpayOrderId;

    private BigDecimal amount;

    private String paymentId;

    private String paymentStatus;

}