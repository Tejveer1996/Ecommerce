package dev.tejveer.Inventory.exception;

public class DeleteInventoryException extends Exception{
    public DeleteInventoryException() {
        super();
    }

    public DeleteInventoryException(String message) {
        super(message);
    }

    public DeleteInventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
