package dev.tejveer.EcomOrderService.kafka.listener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusEventDto {
    private String orderId;
    private UUID paymentId;
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private Long paymentTimeStamp;
}
