package dev.Tejveer.EcomPaymentService.DTO.razorpay;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
    @JsonProperty("created") CREATED,
    @JsonProperty("attempted") ATTEMPTED,
    @JsonProperty("paid") PAID
}
