package dev.tejveer.EcomOrderService.client.cart;

import dev.tejveer.EcomOrderService.client.cart.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "EcomCartService",
        path = "/apis/cart",
        configuration = FeignClient.class
)
public interface CartFeignClient {

    @GetMapping("")
    CartResponse getCartByUserId(String userId);
}
