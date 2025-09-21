package dev.tejveer.EcomOrderService.DTO;

import dev.tejveer.EcomOrderService.Model.OrderItem;
import dev.tejveer.EcomOrderService.Model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderListResponseDTO {
    private Integer statusCode;
    private String userId;
    private List<OrderResponse> response;

    @Data
    @Builder
    public static class OrderResponse {
        private OrderStatus orderStatus;
        private String transactionId;
        private Double totalAmount;
        private List<OrderItem> orderItems;
        private String createdAt;
    }
}
