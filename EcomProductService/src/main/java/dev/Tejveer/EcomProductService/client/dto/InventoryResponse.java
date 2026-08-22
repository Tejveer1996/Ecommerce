package dev.Tejveer.EcomProductService.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    UUID id;
    UUID productId;
    Long availableQuantity;
    Long reservedQuantity;
    Long minimumStock;
    Instant createdAt;
    Instant updatedAt;
}
