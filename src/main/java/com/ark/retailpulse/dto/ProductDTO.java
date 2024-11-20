package com.ark.retailpulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {

    private Long id;
    @NotBlank(message="product name cannot be empty")
    private String name;
    @NotBlank(message="product description cannot be empty")
    private String description;
    @Positive(message="cannot be negative")
    private BigDecimal price;
    @PositiveOrZero(message="quantity cannot negative")
    private Integer quantity;

    private List<CommentDTO> comments;
}
