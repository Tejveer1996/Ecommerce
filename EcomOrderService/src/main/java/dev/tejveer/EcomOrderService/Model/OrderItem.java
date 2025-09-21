package dev.tejveer.EcomOrderService.Model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    String productId;
    String productName;
    Integer quantity;
    Double price;
}
