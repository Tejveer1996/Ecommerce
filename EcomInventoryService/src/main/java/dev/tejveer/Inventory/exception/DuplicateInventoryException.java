package dev.tejveer.Inventory.exception;

public class DuplicateInventoryException extends Exception{
    public DuplicateInventoryException() {
        super();
    }

    public DuplicateInventoryException(String message) {
        super(message);
    }

    public DuplicateInventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
