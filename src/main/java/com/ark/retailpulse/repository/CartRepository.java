package com.ark.retailpulse.repository;

import com.ark.retailpulse.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing Cart entities from the database.
 * Extends JpaRepository to provide CRUD operations and custom queries for Cart entities.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds a Cart by the user's ID.
     * This method is used to retrieve a Cart for a specific user.
     *
     * @param userId the ID of the user
     * @return an Optional containing the Cart if found, otherwise empty
     */
    Optional<Cart> findByUserId(Long userId);
}
