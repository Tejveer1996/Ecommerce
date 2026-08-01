package dev.tejveer.EcomCartService.exception;

public class GetCartException extends Exception {
    public GetCartException() {
        super();
    }

    public GetCartException(String message) {
        super(message);
    }

    public GetCartException(String message, Throwable cause) {
        super(message, cause);
    }
}
