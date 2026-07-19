package dev.tejveer.EcomOrderService.Impl.service;

import dev.tejveer.EcomOrderService.DTO.OrderListResponseDTO;
import dev.tejveer.EcomOrderService.DTO.OrderRequestDTO;
import dev.tejveer.EcomOrderService.DTO.OrderResponseDTO;
import dev.tejveer.EcomOrderService.DTO.PaymentDTO;
import dev.tejveer.EcomOrderService.Exception.CreateOrderException;
import dev.tejveer.EcomOrderService.Exception.OrderListNotFoundException;
import dev.tejveer.EcomOrderService.Exception.OrderNotFoundException;
import dev.tejveer.EcomOrderService.Exception.UpdateOrderException;
import dev.tejveer.EcomOrderService.Interface.IOrderService;
import dev.tejveer.EcomOrderService.Model.OrderStatus;
import dev.tejveer.EcomOrderService.Impl.dao.OrderDAO;
import dev.tejveer.EcomOrderService.Impl.dao.PaymentDAO;
import dev.tejveer.EcomOrderService.Store.OrderStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements IOrderService {
    @Autowired
    private final OrderStore orderStore;

    public OrderService(OrderStore orderStore) {
        this.orderStore = orderStore;
    }

    public OrderResponseDTO createOrder(OrderRequestDTO orderDTO) throws CreateOrderException {
        OrderDAO orderDAO = OrderHelper.mapFromOrderRequestToOrderDao(orderDTO);
        try {
            // TODO : Call the product service to update the inventory.
            // And if out of stock return with out of stock status.
            String orderId = orderStore.storeOrderSummary(orderDAO);
            return OrderResponseDTO.builder()
                    .orderId(orderId)
                    .code(200)
                    .userId(orderDTO.getUserId())
                    .message("Order has been placed successfully for userId : " + orderDTO.getUserId())
                    .totalAmount(orderDAO.getTotalAmount())
                    .build();
        } catch (Exception e) {
            throw new CreateOrderException("Failed to place the order.", e);
        }
    }

    public OrderResponseDTO getOrderById(String orderId) throws OrderNotFoundException {
        return null;
    }

    public OrderListResponseDTO getOrdersByUserId(String userId) throws OrderListNotFoundException {
        try {
            return OrderListResponseDTO.builder()
                    .statusCode(200)
                    .userId(userId)
                    .response(orderStore.getOrderList(userId))
                    .build();
        } catch (Exception e) {
            throw new OrderListNotFoundException("Failed to fetch the order list for userId : " + userId, e);
        }
    }

    public void updateOrderPaymentStatus(String userId, PaymentDTO paymentDTO) throws UpdateOrderException {
        PaymentDAO paymentDAO = PaymentDAO.builder()
                .userId(userId)
                .orderId(paymentDTO.getOrderId())
                .orderStatus(paymentDTO.getPaymentStatus().name().equalsIgnoreCase("failed") ? OrderStatus.IN_PROGRESS.name() :
                        OrderStatus.CONFIRMED.name())
                .paymentStatus(paymentDTO.getPaymentStatus().name())
                .transactionId(paymentDTO.getTransactionId())
                .paymentTimeStamp(paymentDTO.getTimeStamp())
                .build();
        try {
            orderStore.updatePaymentStatus(paymentDAO);
        } catch (Exception e) {
            throw new UpdateOrderException("Failed to update the payment status", e);
        }
    }
}
