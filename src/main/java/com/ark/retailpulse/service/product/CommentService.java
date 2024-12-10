package com.ark.retailpulse.service.product;

import com.ark.retailpulse.dto.product.CommentDTO;
import com.ark.retailpulse.exception.ResourceNotFoundException;
import com.ark.retailpulse.mapper.CommentMapper;
import com.ark.retailpulse.model.Comment;
import com.ark.retailpulse.model.Product;
import com.ark.retailpulse.model.User;
import com.ark.retailpulse.repository.CommentRepository;
import com.ark.retailpulse.repository.ProductRepository;
import com.ark.retailpulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    /**
     * Adds a comment for a specific product by a user.
     *
     * @param productId the product ID
     * @param userId    the user ID
     * @param commentDTO the comment data transfer object
     * @return the saved CommentDTO
     */
    public CommentDTO addComment(Long productId, Long userId, CommentDTO commentDTO) {
        // Retrieve the product by ID or throw exception if not found
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));
        // Retrieve the user by ID or throw exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        // Map DTO to entity, associate with the product and user
        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setProduct(product);
        comment.setUser(user);

        // Save the comment and map it back to DTO
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);

    }
    /**
     * Retrieves all comments for a specific product.
     *
     * @param productId the product ID
     * @return a list of CommentDTOs
     */
    public List<CommentDTO> getCommentsByProduct(Long productId){

        // Retrieve all comments for the product
        List<Comment> comments = commentRepository.findByProductId(productId);

        // Map each comment to DTO
        return comments.stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }



}















