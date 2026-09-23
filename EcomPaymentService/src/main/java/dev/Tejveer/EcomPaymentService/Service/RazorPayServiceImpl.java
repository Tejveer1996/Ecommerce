package dev.Tejveer.EcomPaymentService.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import dev.Tejveer.EcomPaymentService.Config.RazorPayConfig;
import dev.Tejveer.EcomPaymentService.DTO.PaymentRequestDTO;
import dev.Tejveer.EcomPaymentService.DTO.razorpay.PaymentLinkStatus;
import dev.Tejveer.EcomPaymentService.DTO.razorpay.RazorpayWebhookRequest;
import dev.Tejveer.EcomPaymentService.DTO.razorpay.WebhookEvent;
import dev.Tejveer.EcomPaymentService.Entity.Payment;
import dev.Tejveer.EcomPaymentService.Entity.PaymentStatus;
import dev.Tejveer.EcomPaymentService.Repository.PaymentRepository;
import dev.Tejveer.EcomPaymentService.exception.PaymentLinkException;
import dev.Tejveer.EcomPaymentService.exception.ResourceNotFound;
import dev.Tejveer.EcomPaymentService.exception.WebhookHandlerException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Slf4j
public class RazorPayServiceImpl implements IPaymentService {
    @Autowired
    private RazorPayConfig razorPayConfig;
    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public String generatePaymentLink(PaymentRequestDTO paymentRequestDTO) throws PaymentLinkException {
        try {
            Payment savedPayment = new Payment();
            savedPayment.setPaymentStatus(PaymentStatus.PENDING);
            savedPayment.setAmount(BigDecimal.valueOf(paymentRequestDTO.getAmount()));
            savedPayment.setOrderId(paymentRequestDTO.getOrderId());


            RazorpayClient razorpayClient = razorPayConfig.getRazorpayClient();
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", paymentRequestDTO.getAmount()*100);
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("accept_partial", false);
            paymentLinkRequest.put("expire_by", Instant.now().toEpochMilli() + 600000);
            paymentLinkRequest.put("reference_id", paymentRequestDTO.getOrderId());
            paymentLinkRequest.put("description", paymentRequestDTO.getDescription());

            JSONObject customer = new JSONObject();
            customer.put("name", paymentRequestDTO.getCustomerName());
            customer.put("contact", paymentRequestDTO.getCustomerPhone());
            customer.put("email", paymentRequestDTO.getCustomerEmail());
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);
            paymentLinkRequest.put("reminder_enable", true);

            PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);

            savedPayment.setPaymentLinkId(payment.get("id"));
            paymentRepository.save(savedPayment);

            return payment.toString();
        } catch (Exception e) {
            log.error("Error occurred while generating payment link for orderId ::{}, error::{}", paymentRequestDTO.getOrderId(),
                    e.getMessage());
            throw new PaymentLinkException("Failed to generate payment link");
        }

    }

    @Override
    public void handlePaymentWebhook(String rawPayload) throws WebhookHandlerException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RazorpayWebhookRequest webhookRequest = objectMapper.readValue(rawPayload, RazorpayWebhookRequest.class);

            WebhookEvent webhookEvent = webhookRequest.getEvent();
            String paymentLinkId = webhookRequest.getPayload().getPaymentLink().getEntity().getId();
            String orderId = webhookRequest.getPayload().getPaymentLink().getEntity().getReferenceId();
            PaymentLinkStatus paymentStatus = webhookRequest.getPayload().getPaymentLink().getEntity().getStatus();

            String transactionId = webhookRequest.getPayload().getPayment().getEntity().getId();
            BigDecimal amount = BigDecimal.valueOf(webhookRequest.getPayload().getPayment().getEntity().getAmount(), 2100);

            Payment savedPayment = paymentRepository.fetchByPaymentLinkIdAndOrderId(paymentLinkId, orderId).orElseThrow(
                    () -> new ResourceNotFound("Failed to find payment row for paymentLinkId : "+paymentLinkId)
            );

            if (savedPayment.getPaymentStatus() != PaymentStatus.PENDING) {
                log.info("Webhook for paymentLinkId {} already processed, status is {}, skipping", paymentLinkId, savedPayment.getPaymentStatus());
                return;
            }

            savedPayment.setTransactionId(transactionId);

            switch (webhookEvent){
                case  PAYMENT_LINK_PAID -> savedPayment.setPaymentStatus(PaymentStatus.SUCCESS);
                case PAYMENT_LINK_CANCELLED -> savedPayment.setPaymentStatus(PaymentStatus.CANCELLED);
                case PAYMENT_LINK_EXPIRED -> savedPayment.setPaymentStatus(PaymentStatus.EXPIRED);
                default -> savedPayment.setPaymentStatus(PaymentStatus.FAILED);
            }
            paymentRepository.save(savedPayment);
        } catch (Exception e) {
            log.error("Error occurred while operating the webhook response, error ::{}", e.getMessage());
            throw new WebhookHandlerException("Failed to handle webhook response");
        }
    }
}
