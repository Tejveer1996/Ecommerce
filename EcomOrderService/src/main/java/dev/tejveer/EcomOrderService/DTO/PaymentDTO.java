package dev.tejveer.EcomOrderService.DTO;

import dev.tejveer.EcomOrderService.Model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDTO {
    private String orderId;
    private String transactionId;
    private PaymentStatus paymentStatus;
    private String timeStamp;
}
