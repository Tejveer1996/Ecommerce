package dev.tejveer.EcomCartService.exception;

public class CartItemOperationException extends Exception{
    public CartItemOperationException() {
        super();
    }

    public CartItemOperationException(String message) {
        super(message);
    }

    public CartItemOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
