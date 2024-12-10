package com.ark.retailpulse.dto.order;

import com.ark.retailpulse.model.Order;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing an order placed by a user.
 */
@Data
public class OrderDTO  {
   private Long id;
   private Long userId;

   @NotBlank(message = "Address is required")
   private String address;

   @NotBlank(message = "Phone name is required")
   private String phoneNumber;

   private Order.OrderStatus status;
   private LocalDateTime createdAt;
   private List<OrderItemDTO> orderItems;
}