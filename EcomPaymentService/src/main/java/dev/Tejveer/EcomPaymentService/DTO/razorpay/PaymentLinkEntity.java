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
public class PaymentLinkEntity {
    private String id;
    private String referenceId;
    private PaymentLinkStatus status;
    private Long amount;
    private Long amountPaid;
    private Long firstMinPartialAmount;
    private boolean acceptPartial;
    private String currency;
    private String description;
    private Customer customer;
    private NotifyPreference notify;
    private Map<String, Object> notes;
    private String callbackUrl;
    private String callbackMethod;
    private String shortUrl;
    private String orderId;
    private Long expireBy;
    private Long expiredAt;
    private Long cancelledAt;
    private Long createdAt;
    private Long updatedAt;
    private boolean reminderEnable;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {
        private String name;
        private String email;
        private String contact;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NotifyPreference {
        private boolean email;
        private boolean sms;
        private boolean whatsapp;
    }
}
