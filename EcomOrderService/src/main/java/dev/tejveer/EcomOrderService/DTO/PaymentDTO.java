package dev.tejveer.EcomOrderService.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDTO {
    private String orderId;
    private String transactionId;
    private PaymentStatus status;
    private String timeStamp;
}
