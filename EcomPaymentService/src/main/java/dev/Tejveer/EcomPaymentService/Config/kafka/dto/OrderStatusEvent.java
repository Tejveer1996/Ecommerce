package dev.Tejveer.EcomPaymentService.Config.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

// NOTE: deliberately no @JsonNaming(SnakeCaseStrategy) here — the consumer side
// (EcomOrderService's OrderStatusEventDto) has no matching naming strategy configured
// on its JsonDeserializer, so it expects plain camelCase field names. Serializing this
// as snake_case would leave every field null on the consumer side (orderId, status, etc.
// simply wouldn't match "order_id", "status" -> fine, but "orderId" vs "order_id" doesn't),
// which then NPEs in OrderService.updateOrderPaymentStatus() on orderStatusEventDto.getStatus().
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderStatusEvent {
    private String orderId;
    private UUID paymentId;
    private String transactionId;
    private BigDecimal amount;
    private String status;
    private Long paymentTimeStamp;
}
