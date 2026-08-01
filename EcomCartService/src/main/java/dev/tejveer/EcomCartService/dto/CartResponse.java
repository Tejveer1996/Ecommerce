package dev.tejveer.EcomCartService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
