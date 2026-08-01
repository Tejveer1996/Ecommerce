package dev.tejveer.EcomCartService.service.clients;

import dev.tejveer.EcomCartService.service.clients.dto.InventoryCheckRequest;
import dev.tejveer.EcomCartService.service.clients.dto.InventoryCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "EcomInventoryService", path = "/apis/inventory")
public interface InventoryFeignClient {

    @GetMapping("/check-stock")
    InventoryCheckResponse checkStock(InventoryCheckRequest request);
}
