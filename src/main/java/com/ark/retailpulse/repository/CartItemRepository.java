package com.ark.retailpulse.repository;

import com.ark.retailpulse.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    CartItem findByCartId(Long cartId);
    void deleteByCartId(Long cartId);
}
