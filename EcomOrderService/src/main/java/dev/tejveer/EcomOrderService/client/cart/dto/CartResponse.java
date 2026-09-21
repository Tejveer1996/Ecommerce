package dev.tejveer.EcomOrderService.client.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    UUID cartId;
    UUID userId;
    List<CartItemResponse> items;
    Long totalAmount; // sum of all item subtotals
    Instant createdAt;
    Instant updatedAt;

    @Data
    @Builder
    public class CartItemResponse {
        UUID cartItemId;
        UUID productId;
        Integer quantity;
        BigDecimal priceSnapShot;
        BigDecimal subtotal; // priceSnapShot * quantity — computed, not stored
    }
}
