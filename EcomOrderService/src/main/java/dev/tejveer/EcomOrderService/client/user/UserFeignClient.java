package dev.tejveer.EcomOrderService.client.user;

import dev.tejveer.EcomOrderService.client.user.dto.AddressResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@FeignClient(
        name = "EcomUserAuthService",
        path = "apis/user/address",
        configuration = FeignClient.class
)
public interface UserFeignClient {

    @GetMapping("/{addressId}")
    AddressResponse getAddressById(UUID addressId);


}
