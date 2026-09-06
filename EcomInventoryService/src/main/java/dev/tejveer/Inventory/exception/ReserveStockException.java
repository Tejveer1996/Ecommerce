package dev.tejveer.Inventory.exception;

public class ReserveStockException extends Exception{
    public ReserveStockException() {
    }

    public ReserveStockException(String message) {
        super(message);
    }

    public ReserveStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
