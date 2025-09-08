package dev.tejveer.EcomOrderService.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;
    String productId;
    Integer quantity;
    double price;
    @ManyToOne
    @JoinColumn(name = "oder_summary_id")
    OrderSummary orderSummary;
}
