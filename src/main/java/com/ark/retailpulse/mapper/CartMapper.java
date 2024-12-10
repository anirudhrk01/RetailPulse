package com.ark.retailpulse.mapper;


import com.ark.retailpulse.dto.cart.CartDTO;
import com.ark.retailpulse.dto.cart.CartItemDTO;
import com.ark.retailpulse.model.Cart;
import com.ark.retailpulse.model.CartItem;
import com.ark.retailpulse.response.CartDtoDetails.CartDtoDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "userId", source = "user.id")
    CartDTO toDTO(Cart Cart);
    @Mapping(target="user.id", source = "userId")
    Cart toEntity(CartDTO cartDTO);
    
    CartItemDTO toDTO(CartItem cartItem);

    CartItem toEntity(CartItemDTO cartItemDTO);

    CartDtoDetails toDtoDetails(Cart cart);

    Cart toCart(CartDtoDetails cartDtoDetails);
}