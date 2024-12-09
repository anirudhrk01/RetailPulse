package com.ark.retailpulse.response.CartDtoDetails;

import com.ark.retailpulse.dto.cart.CartItemDTO;
import lombok.Data;

import java.util.List;

@Data
public class CartDtoDetails {
    private Long id;
    private Long userId; //usedId
    private List<CartItemsDetails> items;
}
