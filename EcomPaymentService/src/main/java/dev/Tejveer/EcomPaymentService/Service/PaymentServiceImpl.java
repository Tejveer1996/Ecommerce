package dev.Tejveer.EcomPaymentService.Service;

import dev.Tejveer.EcomPaymentService.Config.RazorPayConfig;
import dev.Tejveer.EcomPaymentService.DTO.PaymentRequestDTO;
import dev.Tejveer.EcomPaymentService.Entity.Payment;
import dev.Tejveer.EcomPaymentService.Entity.PaymentStatus;
import dev.Tejveer.EcomPaymentService.Repository.PaymentRepository;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentServiceImpl implements IPaymentService {
    @Autowired
    private RazorPayConfig razorPayConfig;
    @Autowired
    private PaymentRepository paymentRepository;
    @Override
    public String generatePaymentLink(PaymentRequestDTO paymentRequestDTO) throws RazorpayException {
        Payment savedPayment = new Payment();
        savedPayment.setPaymentStatus(PaymentStatus.PENDING);
        savedPayment.setAmount(paymentRequestDTO.getAmount());
        savedPayment.setOrderId(paymentRequestDTO.getOrderId());
        savedPayment.setTransactionId(String.valueOf(paymentRequestDTO.getOrderId().hashCode()));
        paymentRepository.save(savedPayment);


        RazorpayClient razorpayClient = razorPayConfig.getRazorpayClient();
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount",paymentRequestDTO.getAmount());
        paymentLinkRequest.put("currency","INR");
        paymentLinkRequest.put("accept_partial",false);
        paymentLinkRequest.put("expire_by", Instant.now().toEpochMilli() + 600000);
        paymentLinkRequest.put("reference_id", paymentRequestDTO.getOrderId());
        paymentLinkRequest.put("description", paymentRequestDTO.getDescription());
        JSONObject customer = new JSONObject();
        customer.put("name", paymentRequestDTO.getCustomerName());
        customer.put("contact",paymentRequestDTO.getCustomerPhone());
        customer.put("email",paymentRequestDTO.getCustomerEmail());
        paymentLinkRequest.put("customer",customer);
        JSONObject notify = new JSONObject();
        notify.put("sms",true);
        notify.put("email",true);
        paymentLinkRequest.put("notify",notify);
        paymentLinkRequest.put("reminder_enable",true);
        PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
        return payment.toString();

    }
}
