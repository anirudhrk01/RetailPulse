package com.ark.retailpulse.response.CartDtoDetails;

import lombok.Data;

import java.util.List;

@Data
public class CartDtoDetails {
    private Long id;
    private Long userId; 
    private List<CartItemsDetails> items;
}
