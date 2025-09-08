package dev.Tejveer.EcomPaymentService.Controller;

import dev.Tejveer.EcomPaymentService.DTO.PaymentRequestDTO;
import dev.Tejveer.EcomPaymentService.Service.IPaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<String> doPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) throws RazorpayException {
        return ResponseEntity.ok(paymentService.generatePaymentLink(paymentRequestDTO));
    }
}
