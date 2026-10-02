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

    // Cart Service's GET /apis/cart takes no userId — it derives the caller's identity
    // from the forwarded Authorization header (see FeignConfig's RequestInterceptor) via
    // its own JWT filter, the same way OrderController.extractUserId() does locally.
    @GetMapping("")
    CartResponse getCartByUserId();
}
