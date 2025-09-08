package dev.tejveer.EcomOrderService.Entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderSummary extends BaseModel {
    String userId;
    @OneToMany(mappedBy = "orderSummary",cascade = CascadeType.ALL)
    List<OrderItem> orderItems;
    OrderStatus orderStatus;
    String paymentId;
    double totalAmount;
}
