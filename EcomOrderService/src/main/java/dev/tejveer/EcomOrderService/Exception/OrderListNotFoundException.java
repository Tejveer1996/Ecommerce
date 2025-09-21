package dev.tejveer.EcomOrderService.Exception;

public class OrderListNotFoundException extends Exception{
    public OrderListNotFoundException(String message) {
        super(message);
    }

    public OrderListNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
