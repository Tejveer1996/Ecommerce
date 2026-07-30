package dev.tejveer.Inventory.entity.enums;

/**
 *  RESERVED -> Immediately after the Order Service successfully reserves inventory.
 *  CONFIRMED -> After the Payment Service confirms a successful payment.
 *  RELEASED -> Order cancellation, payment failure, or customer cancellation.
 *  EXPIRED -> Triggered by a scheduled Cron job after the reservation timeout.
 */
public enum ReservationStatus {
    RESERVED, CONFIRMED, RELEASED, EXPIRED
}
