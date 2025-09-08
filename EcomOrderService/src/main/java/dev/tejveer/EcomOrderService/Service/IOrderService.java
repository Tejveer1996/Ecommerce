package dev.tejveer.EcomOrderService.Service;

import dev.tejveer.EcomOrderService.DTO.OrderDAO;
import dev.tejveer.EcomOrderService.DTO.OrderDTO;
import dev.tejveer.EcomOrderService.DTO.PaymentDTO;
import dev.tejveer.EcomOrderService.Exception.CreateOrderException;
import dev.tejveer.EcomOrderService.Exception.OrderNotFoundException;
import dev.tejveer.EcomOrderService.Exception.UpdateOrderException;

import java.util.List;

public interface IOrderService {
    OrderDAO createOrder(OrderDTO orderDTO) throws CreateOrderException;

    OrderDAO getOrderById(String orderId) throws OrderNotFoundException;

    List<OrderDAO> getOrderByUserId(String userId) throws OrderNotFoundException;

    void updateOrderPaymentStatus(PaymentDTO paymentDTO) throws UpdateOrderException;
}
