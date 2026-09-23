package dev.Tejveer.EcomPaymentService.DTO.razorpay;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WebhookEvent {
    @JsonProperty("payment_link.paid") PAYMENT_LINK_PAID,
    @JsonProperty("payment_link.partially_paid") PAYMENT_LINK_PARTIALLY_PAID,
    @JsonProperty("payment_link.cancelled") PAYMENT_LINK_CANCELLED,
    @JsonProperty("payment_link.expired") PAYMENT_LINK_EXPIRED
}
