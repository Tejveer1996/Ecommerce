package dev.tejveer.EcomOrderService.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderDTO {
    private String userId;
    private List<Item> orderItems;

    @Data
    @Builder
    public static class Item {
        private String productId;
        private String productName;
        private Integer quantity;
        private Double price;
    }
}
