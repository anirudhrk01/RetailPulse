package com.ark.retailpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a shopping cart associated with a user.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The user to whom this cart belongs.
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    /**
     * List of items in the cart.
     *
     * Items are automatically removed when detached from the cart.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}
