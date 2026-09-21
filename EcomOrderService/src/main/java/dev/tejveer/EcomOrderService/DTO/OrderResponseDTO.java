package dev.tejveer.EcomOrderService.DTO;

import dev.tejveer.EcomOrderService.Model.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponseDTO {
    String orderId;
    String userId;
    OrderStatus orderStatus;
    BigDecimal totalAmount;
    String message;
    Integer code;
}
