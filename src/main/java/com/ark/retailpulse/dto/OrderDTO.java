package com.ark.retailpulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import com.ark.retailpulse.model.Order;

@Data
public class OrderDTO {
 private Long id;
 private Long userId;
 @NotBlank(message = "address cannot be null")
 private String address;
 @NotBlank(message = "phonenumber cannot be null")
 private String phoneNumber;
 private Order.OrderStatus status;
 private LocalDateTime createdAt;
 private List<OrderItemDTO> orderItems;

}
