package dev.Tejveer.EcomPaymentService.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequestDTO {
    private Long amount;
    private String orderId;
    private String description;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
}
