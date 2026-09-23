package dev.Tejveer.EcomPaymentService.Entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "Payment_Table")
public class Payment extends BaseModel{
    private BigDecimal amount;
    private UUID userId;
    private String orderId;
    private String transactionId;
    private String paymentLinkId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String currency;
}
