package dev.Tejveer.EcomProductService.client;

import dev.Tejveer.EcomProductService.client.dto.InventoryCreateRequest;
import dev.Tejveer.EcomProductService.client.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "EcomInventoryService",
        path = "/apis/inventory",
        configuration = FeignConfig.class
)
public interface InventoryFeignClient {

    @PostMapping("/create")
    InventoryResponse initializeInventory(InventoryCreateRequest createRequest);
}
