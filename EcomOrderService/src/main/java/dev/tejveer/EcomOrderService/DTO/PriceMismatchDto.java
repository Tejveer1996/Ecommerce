package dev.tejveer.EcomOrderService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PriceMismatchDto {
    private String productId;
    private BigDecimal cartPrice;
    private BigDecimal currentPrice;
}
