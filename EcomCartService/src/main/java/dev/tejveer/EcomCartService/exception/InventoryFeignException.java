package dev.tejveer.EcomCartService.exception;

public class InventoryFeignException extends Exception {
    public InventoryFeignException() {
        super();
    }

    public InventoryFeignException(String message) {
        super(message);
    }

    public InventoryFeignException(String message, Throwable cause) {
        super(message, cause);
    }
}
