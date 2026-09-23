package dev.Tejveer.EcomPaymentService.Controller;

import com.razorpay.RazorpayException;
import dev.Tejveer.EcomPaymentService.DTO.PaymentRequestDTO;
import dev.Tejveer.EcomPaymentService.Service.IPaymentService;
import dev.Tejveer.EcomPaymentService.exception.WebhookHandlerException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Tag(
        name = "Payment APIs",
        description = "Operations related to payment initiation"
)
@RestController
@Slf4j
@RequestMapping("/apis/payment")
public class PaymentController {

    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Initiate payment", description = "Generate a Razorpay payment link for the given order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment Link Generated"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order Not Found", content = @Content),
            @ApiResponse(responseCode = "502", description = "Payment Gateway Error", content = @Content)
    })
    @PostMapping("/pay")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> doPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            String paymentLink = paymentService.generatePaymentLink(paymentRequestDTO);
            return ResponseEntity.ok(paymentLink);
        } catch (Exception e) {
            log.error("Error occurred while generating payment link for orderId :{}, error :: {}",
                    paymentRequestDTO.getOrderId(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Razorpay webhook", description = "Receives payment status updates from Razorpay")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook Processed Successfully"),
            @ApiResponse(responseCode = "500", description = "Webhook Processing Failed", content = @Content)
    })
    @PostMapping("/webhook")
    public ResponseEntity<Void> handlePaymentWebhook(@RequestBody String rawPayload) {
        try {
            paymentService.handlePaymentWebhook(rawPayload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error occurred while handling payment webhook, error :: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private UUID getCurrentUserId() {
        try {
            String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return UUID.fromString(principal);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing user identity");
        }
    }
}
