package com.ark.retailpulse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Represents a comment made by a user on a product.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Integer score;

    /**
     * The product associated with this comment.
     */
    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    /**
     * The user who posted the comment.
     */
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;
}