package com.ark.retailpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name= "user_Id", nullable =false)
    private User user;

    private String address;
    private String phoneNumber;
    private OrderStatus status;

    public enum OrderStatus{
        PREPARING,DELIVERING,DELIVERED,CANCELLED;
    }
    @OneToMany(mappedBy="orders",cascade = CascadeType.ALL , orphanRemoval = true )
    private List<OrderItem> items = new ArrayList<>();
}
