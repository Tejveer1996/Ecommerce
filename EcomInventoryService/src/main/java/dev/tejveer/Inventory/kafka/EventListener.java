package dev.tejveer.Inventory.kafka;

import dev.tejveer.Inventory.dto.ConfirmReserveStockRequest;
import dev.tejveer.Inventory.exception.InventoryReservationException;
import dev.tejveer.Inventory.kafka.dto.OrderConfirmEventDto;
import dev.tejveer.Inventory.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventListener {
    private final InventoryReservationService inventoryReservationService;

    @KafkaListener(
            topics = "${app.kafka.topic.order-confirm-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void orderConfirmListener(OrderConfirmEventDto orderConfirmEventDto) throws InventoryReservationException {
        inventoryReservationService.confirmReserveStock(ConfirmReserveStockRequest.builder()
                .orderId(UUID.fromString(orderConfirmEventDto.getOrderId()))
                .build());
    }
}
