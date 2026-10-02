package dev.tejveer.EcomOrderService.Impl.dao;

import dev.tejveer.EcomOrderService.Model.OrderItem;
import dev.tejveer.EcomOrderService.Model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderDAO {
    private String orderId;
    private String userId;
    private String address;
    private List<OrderItem> orderItems;
    private OrderStatus orderStatus;
    private String transactionId;
    private BigDecimal totalAmount;
}
