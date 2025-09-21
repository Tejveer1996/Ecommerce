package dev.tejveer.EcomOrderService.Impl.dao;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDAO {
    String userId;
    String orderId;
    String orderStatus;
    String transactionId;
    String paymentStatus;
    String paymentTimeStamp;
}
