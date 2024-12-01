package com.ark.retailpulse.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductListDTO {

    private Long id;
    @NotBlank(message = "cannot be empty")
    private String name;
    @NotBlank(message = "cannot be empty")
    private String description;
    @Positive(message = "only positive")
    private BigDecimal price;
    @PositiveOrZero(message = "quantity must be positive or zero")
    private Integer quantity;
    private String image;

}
