package dev.tejveer.EcomOrderService.DTO;

import dev.tejveer.EcomOrderService.Model.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponseDTO {
    String orderId;
    String userId;
    OrderStatus orderStatus;
    Double totalAmount;
    String message;
    Integer code;
}
