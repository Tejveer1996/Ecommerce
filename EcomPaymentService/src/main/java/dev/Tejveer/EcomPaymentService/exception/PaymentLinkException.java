package dev.Tejveer.EcomPaymentService.exception;

public class PaymentLinkException extends Exception{
    public PaymentLinkException() {
    }

    public PaymentLinkException(String message) {
        super(message);
    }

    public PaymentLinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
