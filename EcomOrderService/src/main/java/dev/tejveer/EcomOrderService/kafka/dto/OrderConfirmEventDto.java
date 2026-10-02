package dev.tejveer.EcomOrderService.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderConfirmEventDto {
    private String orderId;
    private String userId;
    private BigDecimal totalAmount;
    private Long confirmedAt;   // epoch millis
}
