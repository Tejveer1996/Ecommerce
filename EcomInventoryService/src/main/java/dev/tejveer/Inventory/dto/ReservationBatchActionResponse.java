package dev.tejveer.Inventory.dto;

import dev.tejveer.Inventory.entity.enums.ReservationStatus;
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
public class ReservationBatchActionResponse {

    UUID orderId;
    ReservationStatus resultingStatus; // CONFIRMED or RELEASED
    int itemsProcessed;
    List<UUID> reservationIds;
    String message;
}
