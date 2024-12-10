package com.ark.retailpulse.helper.order;

import com.ark.retailpulse.model.Cart;
import com.ark.retailpulse.model.Order;
import com.ark.retailpulse.model.OrderItem;
import com.ark.retailpulse.model.Product;
import com.ark.retailpulse.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for managing order items.
 */
@Component
@RequiredArgsConstructor
public class OrderItemHelper {

    private final ProductRepository productRepository;

    public List<OrderItem> createOrderItems(Cart cart, Order order) {
        return cart.getItems().stream().map(cartItem -> {
            // Fetch the product by ID or throw an exception if not found
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + cartItem.getProduct().getId()));
            // Validate stock availability
            if (product.getQuantity() == null || product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            // Deduct the ordered quantity from the product's stock
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            // Create and return an OrderItem
            return new OrderItem(null, order, product, cartItem.getQuantity(), product.getPrice());
        }).collect(Collectors.toList());
    }
}
