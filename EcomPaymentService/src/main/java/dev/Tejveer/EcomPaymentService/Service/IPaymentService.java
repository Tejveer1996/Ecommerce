package dev.Tejveer.EcomPaymentService.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.Tejveer.EcomPaymentService.DTO.PaymentRequestDTO;
import com.razorpay.RazorpayException;
import dev.Tejveer.EcomPaymentService.exception.PaymentLinkException;
import dev.Tejveer.EcomPaymentService.exception.WebhookHandlerException;

public interface IPaymentService {
    String generatePaymentLink(PaymentRequestDTO paymentRequestDTO) throws PaymentLinkException;

    void handlePaymentWebhook(String rawPayload) throws WebhookHandlerException;
}
