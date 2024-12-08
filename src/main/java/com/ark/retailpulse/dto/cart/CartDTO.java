package com.ark.retailpulse.dto.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {//edited
    private Long id;
    private Long userId; //usedId
    private List<CartItemDTO> items;
}