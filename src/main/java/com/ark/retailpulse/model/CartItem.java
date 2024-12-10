package com.ark.retailpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Represents an item in a shopping cart.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        /**
         * The cart to which this item belongs.
         */
        @ManyToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "cart_id", nullable = false)
        private Cart cart;

        /**
         * The product associated with this cart item.
         */
        @ManyToOne(fetch=FetchType.EAGER)
        @JoinColumn(name="product_id", nullable = false)
        private Product product;

        /**
         * The quantity of the product in the cart.
         */
        private Integer quantity;
}