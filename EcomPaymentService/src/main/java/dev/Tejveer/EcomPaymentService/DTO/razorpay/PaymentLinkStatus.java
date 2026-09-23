package dev.Tejveer.EcomPaymentService.DTO.razorpay;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentLinkStatus {
    @JsonProperty("created") CREATED,
    @JsonProperty("paid") PAID,
    @JsonProperty("partially_paid") PARTIALLY_PAID,
    @JsonProperty("cancelled") CANCELLED,
    @JsonProperty("expired") EXPIRED
}
