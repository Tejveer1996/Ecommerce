package dev.Tejveer.EcomPaymentService.DTO.razorpay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentEntity {
    private String id;
    private String orderId;
    private PaymentStatus status;
    private Long amount;
    private Long amountCaptured;
    private Long amountRefunded;
    private boolean captured;
    private String currency;
    private String method;
    private String description;
    private String email;
    private String contact;
    private Long fee;
    private Long tax;
    private String errorCode;
    private String errorDescription;
    private Map<String, Object> notes;
    private Long createdAt;
    private Card card;
    private String vpa;
    private String bank;
    private String wallet;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        private String id;
        private String last4;
        private String network;
        private String type;
        private String issuer;
    }
}
