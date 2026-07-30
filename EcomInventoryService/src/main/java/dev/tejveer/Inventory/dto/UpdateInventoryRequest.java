package dev.tejveer.Inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInventoryRequest {

    @NotNull(message = "Product id is required")
    UUID productId;

    @Min(value = 0, message = "Available quantity cannot be negative")
    Long availableQuantity;

    @Min(value = 0, message = "Reserved quantity cannot be negative")
    Long reservedQuantity;

    @Min(value = 0, message = "Minimum stock cannot be negative")
    Long minimumStock;
}
