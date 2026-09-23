package dev.Tejveer.EcomPaymentService.DTO.razorpay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Razorpay wraps every nested object in the webhook payload as { "entity": {...} }.
 * This mirrors that shape generically instead of repeating it per type.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntityWrapper<T> {
    private T entity;
}
