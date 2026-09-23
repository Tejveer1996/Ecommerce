package dev.Tejveer.EcomPaymentService.DTO.razorpay;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentStatus {
    @JsonProperty("created") CREATED,
    @JsonProperty("authorized") AUTHORIZED,
    @JsonProperty("captured") CAPTURED,
    @JsonProperty("refunded") REFUNDED,
    @JsonProperty("failed") FAILED
}
