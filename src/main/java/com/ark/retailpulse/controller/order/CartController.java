package com.ark.retailpulse.controller.order;

import com.ark.retailpulse.model.User;
import com.ark.retailpulse.response.CartDtoDetails.CartDtoDetails;
import com.ark.retailpulse.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * CartController handles cart-related operations such as adding items to the cart,
 * retrieving the cart, and clearing the cart for authenticated users.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Slf4j // Logger for logging key actions in the controller
public class CartController {

    private final CartService cartService;

    /**
     * Adds a product to the user's cart.
     * @param userDetails the authenticated user
     * @param productId the ID of the product to add
     * @param quantity the quantity to add
     * @return the updated cart details
     */
    @PostMapping("/add")
    public ResponseEntity<CartDtoDetails> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestParam Long productId,
                                                    @RequestParam Integer quantity) {
        Long userId = ((User) userDetails).getId(); // Get the user ID from the authenticated user
        log.info("User with ID: {} is adding product with ID: {} to the cart with quantity: {}", userId, productId, quantity); // Log the action

        CartDtoDetails cart = cartService.addToCart(userId, productId, quantity); // Add product to cart
        log.info("Product with ID: {} added to cart for user with ID: {}", productId, userId); // Log success

        return ResponseEntity.ok(cart); // Return the updated cart
    }

    /**
     * Retrieves the user's cart.
     * @param userDetails the authenticated user
     * @return the user's cart details
     */
    @GetMapping
    public ResponseEntity<CartDtoDetails> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((User) userDetails).getId(); // Get the user ID from the authenticated user
        log.info("Fetching cart for user with ID: {}", userId); // Log the action

        CartDtoDetails cart = cartService.getCart(userId); // Get the user's cart
        return ResponseEntity.ok(cart); // Return the cart details
    }

    /**
     * Clears the user's cart.
     * @param userDetails the authenticated user
     * @return a response indicating the cart has been cleared
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((User) userDetails).getId(); // Get the user ID from the authenticated user
        log.info("Clearing cart for user with ID: {}", userId); // Log the action

        cartService.clearCart(userId); // Clear the user's cart
        log.info("Cart cleared for user with ID: {}", userId); // Log success

        return ResponseEntity.noContent().build(); // Return no content response after clearing the cart
    }
}
