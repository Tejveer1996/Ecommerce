package dev.tejveer.EcomCartService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    UUID cartItemId;
    UUID productId;
    Integer quantity;
    Long priceSnapShot;
    Long subtotal; // priceSnapShot * quantity — computed, not stored
}
