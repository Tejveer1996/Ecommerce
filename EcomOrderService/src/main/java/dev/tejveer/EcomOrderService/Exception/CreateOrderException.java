package dev.tejveer.EcomOrderService.Exception;

public class CreateOrderException extends Exception{
    public CreateOrderException(String message) {
        super(message);
    }

    public CreateOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
