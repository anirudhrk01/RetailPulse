package com.ark.retailpulse.controller.order;

import com.ark.retailpulse.dto.cart.CartDTO;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.response.CartDtoDetails.CartDtoDetails;
import com.ark.retailpulse.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDtoDetails> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam Long productId,
                                             @RequestParam Integer quantity){
        Long userId = ((User) userDetails ).getId();
        return ResponseEntity.ok(cartService.addToCart(userId,productId,quantity));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDtoDetails> getCart(@AuthenticationPrincipal UserDetails userDetails){
        Long userId = ((User) userDetails).getId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails){
        Long userId = ((User) userDetails).getId();
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

}












