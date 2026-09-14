package dev.tejveer.Inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationActionResponse {
    String reservationId;
    String orderId;
    String status; // CONFIRMED or RELEASED
    int itemsProcessed;
    String message;
}
