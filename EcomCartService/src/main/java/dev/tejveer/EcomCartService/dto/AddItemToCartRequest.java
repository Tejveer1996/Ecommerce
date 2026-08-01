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
public class AddItemToCartRequest {
    UUID productId;
    Integer quantity;
    Long priceSnapShot;
}
