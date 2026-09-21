package dev.tejveer.EcomOrderService.client.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReserveResponse {
    String orderId;
    String reservationId;
    List<String> reservedItemIds;
}
