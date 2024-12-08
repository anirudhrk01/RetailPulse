package com.ark.retailpulse.service.cart;

import com.ark.retailpulse.dto.cart.CartDTO;
import com.ark.retailpulse.exception.InsufficientStockException;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.mapper.CartMapper;
import com.ark.retailpulse.model.Cart;
import com.ark.retailpulse.model.CartItem;
import com.ark.retailpulse.model.Product;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.repository.CartRepository;
import com.ark.retailpulse.repository.ProductRepository;
import com.ark.retailpulse.repository.UserRepository;
import com.ark.retailpulse.response.CartDtoDetails.CartDtoDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartDtoDetails addToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));


        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));
        if (product.getPrice() == null) {
            throw new IllegalStateException("Product price is not set");
        }

        if(product.getQuantity()<quantity){
            throw new InsufficientStockException("Quantity exceeds available stock quantity");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(null,user,new ArrayList<>()));

        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item-> item.getProduct().getId().equals(productId))
                .findFirst();

        if(existingCartItem.isPresent()){
           CartItem cartItem = existingCartItem.get();
           cartItem.setQuantity(cartItem.getQuantity()+quantity);

        }
        else {
            CartItem cartItem = new CartItem(null, cart, product, quantity);
            cart.getItems().add(cartItem);
        }
        Cart savedCart = cartRepository.save(cart);
       CartDtoDetails cartDtoDetails= cartMapper.toDtoDetails(savedCart);
       cartDtoDetails.setUserId(user.getId());
        return cartDtoDetails;
    }

    public CartDtoDetails getCart(Long userId){
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart not found"));
        CartDtoDetails cartDtoDetails=cartMapper.toDtoDetails(cart);
        cartDtoDetails.setUserId(cart.getUser().getId());
        return cartDtoDetails;
    }

    public void clearCart(Long userId){
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);

    }


}


















