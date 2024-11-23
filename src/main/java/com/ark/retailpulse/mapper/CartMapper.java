package com.ark.retailpulse.mapper;


import com.ark.retailpulse.dto.CartDTO;
import com.ark.retailpulse.dto.CartItemDTO;
import com.ark.retailpulse.model.Cart;
import com.ark.retailpulse.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "userId", source = "user.id")
    CartDTO toDTO(Cart Cart);
    @Mapping(target="user.id", source = "userId")
    Cart toEntity(CartDTO cartDTO);

    @Mapping(target="productId", source="product.id")
    CartItemDTO toDTO(CartItem cartItem);

    @Mapping(target="product.id", source="productId")
    CartItem toEntity(CartItemDTO cartItemDTO);
}