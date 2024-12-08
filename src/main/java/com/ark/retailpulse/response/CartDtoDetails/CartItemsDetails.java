package com.ark.retailpulse.response.CartDtoDetails;

import com.ark.retailpulse.dto.product.ProductDTO;
import lombok.Data;

@Data
public class CartItemsDetails {
    private Long id;

    private ProductDTO product;

    private Integer quantity;
}
