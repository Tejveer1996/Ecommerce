package dev.Tejveer.EcomPaymentService.DTO.razorpay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEntity {
    private String id;
    private OrderStatus status;
    private Long amount;
    private Long amountPaid;
    private Long amountDue;
    private String currency;
    private String receipt;
    private int attempts;
    private boolean partialPayment;
    private Map<String, Object> notes;
    private Long createdAt;
    private Long updatedAt;
}
