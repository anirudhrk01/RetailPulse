package com.ark.retailpulse.service.cart;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    /**
     * Adds a product to the user's cart. Creates a new cart if none exists.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product to add
     * @param quantity  the quantity of the product to add
     * @return the details of the updated cart
     */
    public CartDtoDetails addToCart(Long userId, Long productId, Integer quantity) {
        logger.info("Adding product with ID {} to cart for user with ID {}", productId, userId);

        // Fetch the user and validate existence
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new ResourceNotFoundException("User not found");
                });

        // Fetch the product and validate availability and price
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product with ID {} not found", productId);
                    return new ResourceNotFoundException("Product not found");
                });
        if (product.getPrice() == null) {
            logger.error("Product with ID {} does not have a price set", productId);
            throw new IllegalStateException("Product price is not set");
        }
        if (product.getQuantity() < quantity) {
            logger.error("Requested quantity {} exceeds available stock for product ID {}", quantity, productId);
            throw new InsufficientStockException("Quantity exceeds available stock quantity");
        }

        // Fetch or create the cart for the user
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(null, user, new ArrayList<>()));

        // Check if the product already exists in the cart
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Update the quantity of the existing cart item
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            logger.info("Updated quantity for product ID {} in cart for user ID {}", productId, userId);
        } else {
            // Add a new item to the cart
            CartItem cartItem = new CartItem(null, cart, product, quantity);
            cart.getItems().add(cartItem);
            logger.info("Added new product ID {} to cart for user ID {}", productId, userId);
        }

        // Save the updated cart to the repository
        Cart savedCart = cartRepository.save(cart);
        CartDtoDetails cartDtoDetails = cartMapper.toDtoDetails(savedCart);
        cartDtoDetails.setUserId(user.getId());
        logger.info("Cart updated successfully for user ID {}", userId);

        return cartDtoDetails;
    }

    /**
     * Retrieves the user's cart.
     *
     * @param userId the ID of the user
     * @return the details of the user's cart
     */
    public CartDtoDetails getCart(Long userId) {
        logger.info("Fetching cart for user ID {}", userId);

        // Fetch the cart for the user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Cart not found for user ID {}", userId);
                    return new ResourceNotFoundException("Cart not found");
                });

        CartDtoDetails cartDtoDetails = cartMapper.toDtoDetails(cart);
        cartDtoDetails.setUserId(cart.getUser().getId());
        logger.info("Cart retrieved successfully for user ID {}", userId);

        return cartDtoDetails;
    }

    /**
     * Clears all items from the user's cart.
     *
     * @param userId the ID of the user
     */
    public void clearCart(Long userId) {
        logger.info("Clearing cart for user ID {}", userId);

        // Fetch the cart for the user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Cart not found for user ID {}", userId);
                    return new ResourceNotFoundException("Cart not found");
                });

        // Clear the cart items and save
        cart.getItems().clear();
        cartRepository.save(cart);
        logger.info("Cart cleared successfully for user ID {}", userId);
    }
}
