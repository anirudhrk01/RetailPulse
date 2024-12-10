package com.ark.retailpulse.dto.cart;

import com.ark.retailpulse.dto.product.ProductDTO;
import lombok.Data;

/**
 * DTO representing an item in the shopping cart.
 */
@Data
public class CartItemDTO {

    private Long id;

    private CartDTO cart;

    private ProductDTO product;

    private Integer quantity;
}