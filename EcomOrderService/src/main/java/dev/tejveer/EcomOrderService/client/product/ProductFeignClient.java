package dev.tejveer.EcomOrderService.client.product;

import dev.tejveer.EcomOrderService.client.product.dto.ProductBriefDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "EcomProductService",
        path = "/apis/products",
        configuration = FeignClient.class
)
public interface ProductFeignClient {
    @GetMapping("")
    List<ProductBriefDto> getProductBriefs(@RequestBody List<UUID> productIds);
}
