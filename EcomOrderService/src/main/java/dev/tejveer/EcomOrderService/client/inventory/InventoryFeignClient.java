package dev.tejveer.EcomOrderService.client.inventory;

import dev.tejveer.EcomOrderService.client.inventory.dto.InventoryReserveRequest;
import dev.tejveer.EcomOrderService.client.inventory.dto.InventoryReserveResponse;
import dev.tejveer.EcomOrderService.client.inventory.dto.ReservationActionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "EcomInventoryService",
        path = "/apis/inventory/reservation",
        configuration = FeignClient.class
)
public interface InventoryFeignClient {

    @PostMapping("/reserve")
    InventoryReserveResponse reserveStock(@RequestBody InventoryReserveRequest request);

    @PostMapping("/release/{reservationId}")
    ReservationActionResponse releaseReserveStock(@PathVariable String reservationId);
}
