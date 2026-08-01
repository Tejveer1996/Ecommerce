package dev.tejveer.EcomCartService.exception;

public class CartCreationException extends Exception{
    public CartCreationException() {
        super();
    }

    public CartCreationException(String message) {
        super(message);
    }

    public CartCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
