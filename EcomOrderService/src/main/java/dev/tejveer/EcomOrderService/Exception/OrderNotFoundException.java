package dev.tejveer.EcomOrderService.Exception;

public class OrderNotFoundException extends Exception{
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
