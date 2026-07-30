package dev.tejveer.Inventory.exception;

public class InventoryReservationException extends Exception{
    public InventoryReservationException() {
        super();
    }

    public InventoryReservationException(String message) {
        super(message);
    }

    public InventoryReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}
