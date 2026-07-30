package dev.tejveer.Inventory.exception;

public class UpdateInventoryException extends Exception{
    public UpdateInventoryException() {
        super();
    }

    public UpdateInventoryException(String message) {
        super(message);
    }

    public UpdateInventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
