package com.ark.retailpulse.dto.cart;

import lombok.Data;

import java.util.List;

/**
 * DTO for representing a user's shopping cart.
 */
@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private List<CartItemDTO> items;
}