package dev.tejveer.EcomOrderService.DTO;

import dev.tejveer.EcomOrderService.Entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderDAO {
    private String orderId;
    private String userId;
    private List<Item> orderItems;
    private OrderStatus orderStatus;
    private String transactionId;
    private Double totalAmount;

    @Data
    @Builder
    public static class Item {
        private String productId;
        private String productName;
        private Integer quantity;
        private Double price;
    }
}
