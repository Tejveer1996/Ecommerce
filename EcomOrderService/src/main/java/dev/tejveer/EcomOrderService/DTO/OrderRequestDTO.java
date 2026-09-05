package dev.tejveer.EcomOrderService.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequestDTO {
    String addressId;
}
