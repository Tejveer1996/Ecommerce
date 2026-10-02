package dev.tejveer.EcomOrderService.Impl.service;

import dev.tejveer.EcomOrderService.DTO.OrderRequestDTO;
import dev.tejveer.EcomOrderService.Impl.dao.OrderDAO;
import dev.tejveer.EcomOrderService.Model.OrderItem;
import dev.tejveer.EcomOrderService.Model.OrderStatus;
import dev.tejveer.EcomOrderService.Utils.Utils;
import dev.tejveer.EcomOrderService.client.cart.dto.CartResponse;
import dev.tejveer.EcomOrderService.client.product.dto.ProductBriefDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderHelper {
    public static OrderDAO mapFromOrderRequestToOrderDao(String userId, UUID orderId, OrderRequestDTO.AddressDto address,
                                                         List<ProductBriefDto> products, CartResponse cart) {
        Map<String, ProductBriefDto> productBriefDtoMap = products.stream()
                .collect(Collectors.toMap(ProductBriefDto::getProductId, Function.identity()));

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .productId(cartItem.getProductId().toString())
                        .quantity(cartItem.getQuantity())
                        .price(productBriefDtoMap.get(cartItem.getProductId().toString()).getPrice())
                        .productName(productBriefDtoMap.get(cartItem.getProductId().toString()).getProductName())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderDAO.builder()
                .orderId(orderId.toString())
                .userId(userId)
                .orderStatus(OrderStatus.IN_PROGRESS)
                .address(Utils.gson.toJson(address))
                .orderItems(orderItems)
                .totalAmount(totalAmount)
                .build();
    }
}
