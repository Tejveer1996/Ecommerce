package dev.tejveer.EcomOrderService.Impl.service;

import dev.tejveer.EcomOrderService.DTO.OrderRequestDTO;
import dev.tejveer.EcomOrderService.Model.OrderItem;
import dev.tejveer.EcomOrderService.Model.OrderStatus;
import dev.tejveer.EcomOrderService.Impl.dao.OrderDAO;

import java.util.stream.Collectors;

public class OrderHelper {
    public static OrderDAO mapFromOrderRequestToOrderDao(OrderRequestDTO orderRequestDTO){
        return OrderDAO.builder()
                .userId(orderRequestDTO.getUserId())
                .orderItems(orderRequestDTO.getOrderItems().stream()
                        .map(item -> OrderItem.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .orderStatus(OrderStatus.IN_PROGRESS)
                .transactionId(null)
                .totalAmount(orderRequestDTO.getOrderItems().stream()
                        .mapToDouble(item -> item.getPrice())
                        .sum())
                .build();
    }
}
