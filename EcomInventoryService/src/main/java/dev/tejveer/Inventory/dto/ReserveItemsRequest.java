package dev.tejveer.Inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveItemsRequest {
    UUID orderId;
    List<Item> items;

    @Data
    @Builder
    public static class Item {
        UUID sellerId;
        UUID productId;
        Long quantity;
    }
}
