package com.ark.retailpulse.service;

import com.ark.retailpulse.dto.CommentDTO;
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

    public CommentDTO addComment(Long productId, Long userId, CommentDTO commentDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setProduct(product);
        comment.setUser(user);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);

    }

    public List<CommentDTO> getCommentsByProduct(Long productId){
            List<Comment> comments = commentRepository.findByProductId(productId);
            return comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());
    }



}















