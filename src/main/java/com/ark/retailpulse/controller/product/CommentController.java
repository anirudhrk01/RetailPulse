package com.ark.retailpulse.controller.product;

import com.ark.retailpulse.dto.product.CommentDTO;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.service.product.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;

    /**
     * Adds a comment to a specific product.
     *
     * @param productId   ID of the product
     * @param userDetails Authenticated user's details
     * @param commentDTO  Comment data
     * @return ResponseEntity containing the created comment
     */

    @PostMapping("/product/{productId}")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long productId,
                                                 @AuthenticationPrincipal UserDetails userDetails,
                                                 @Valid @RequestBody CommentDTO commentDTO){
        logger.info("Attempting to add comment for productId: {}", productId);
        Long userId = ((User) userDetails).getId();
        return ResponseEntity.ok(commentService.addComment(productId, userId, commentDTO));
    }
    /**
     * Retrieves all comments for a specific product.
     *
     * @param productId ID of the product
     * @return ResponseEntity containing the list of comments
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByProduct(@PathVariable Long productId){
        logger.info("Fetching comments for productId: {}", productId);
        List<CommentDTO> comments = commentService.getCommentsByProduct(productId);
        logger.info("Retrieved {} comments for productId: {}", comments.size(), productId);
        return ResponseEntity.ok(comments);
    }

}
























