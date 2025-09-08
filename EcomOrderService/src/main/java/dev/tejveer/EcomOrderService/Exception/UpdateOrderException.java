package dev.tejveer.EcomOrderService.Exception;

public class UpdateOrderException extends Exception{
    public UpdateOrderException(String message) {
        super(message);
    }

    public UpdateOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
