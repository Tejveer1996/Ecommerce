package dev.tejveer.EcomCartService.service.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCheckRequest {
    UUID productId;
    Long requestedQuantity;
}
