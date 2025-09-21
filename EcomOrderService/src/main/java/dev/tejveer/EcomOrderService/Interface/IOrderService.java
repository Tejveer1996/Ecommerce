package dev.tejveer.EcomOrderService.Interface;

import dev.tejveer.EcomOrderService.DTO.OrderListResponseDTO;
import dev.tejveer.EcomOrderService.DTO.OrderRequestDTO;
import dev.tejveer.EcomOrderService.DTO.OrderResponseDTO;
import dev.tejveer.EcomOrderService.DTO.PaymentDTO;
import dev.tejveer.EcomOrderService.Exception.CreateOrderException;

import dev.tejveer.EcomOrderService.Exception.OrderListNotFoundException;
import dev.tejveer.EcomOrderService.Exception.OrderNotFoundException;
import dev.tejveer.EcomOrderService.Exception.UpdateOrderException;

public interface IOrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderDTO) throws CreateOrderException;

    OrderResponseDTO getOrderById(String orderId) throws OrderNotFoundException;

    OrderListResponseDTO getOrdersByUserId(String userId) throws OrderListNotFoundException;

    void updateOrderPaymentStatus(String userId, PaymentDTO paymentDTO) throws UpdateOrderException;
}
