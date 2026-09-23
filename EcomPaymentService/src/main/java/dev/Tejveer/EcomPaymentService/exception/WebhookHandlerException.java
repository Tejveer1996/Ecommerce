package dev.Tejveer.EcomPaymentService.exception;

public class WebhookHandlerException extends Exception{
    public WebhookHandlerException() {
    }

    public WebhookHandlerException(String message) {
        super(message);
    }

    public WebhookHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
