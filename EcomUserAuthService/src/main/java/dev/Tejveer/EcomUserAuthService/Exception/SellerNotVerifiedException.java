package dev.Tejveer.EcomUserAuthService.Exception;

public class SellerNotVerifiedException extends Exception{
    public SellerNotVerifiedException() {
        super();
    }

    public SellerNotVerifiedException(String message) {
        super(message);
    }

    public SellerNotVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}
