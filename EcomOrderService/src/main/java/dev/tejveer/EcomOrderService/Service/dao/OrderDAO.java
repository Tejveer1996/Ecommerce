package dev.tejveer.EcomOrderService.Service.dao;

import dev.tejveer.EcomOrderService.Model.OrderItem;
import dev.tejveer.EcomOrderService.Model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderDAO {
    private String userId;
    private List<OrderItem> orderItems;
    private OrderStatus orderStatus;
    private String transactionId;
    private Double totalAmount;
}
