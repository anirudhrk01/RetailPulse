package com.ark.retailpulse.dto.cart;

import com.ark.retailpulse.dto.product.ProductDTO;
import com.ark.retailpulse.model.Cart;
import com.ark.retailpulse.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemDTO {

    private Long id;

    private CartDTO cart; //to be edited

    private ProductDTO product;

    private Integer quantity;
}