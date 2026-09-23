package dev.Tejveer.EcomPaymentService.Repository;

import dev.Tejveer.EcomPaymentService.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> fetchByPaymentLinkIdAndOrderId(String paymentLinkId, String orderId);
}
