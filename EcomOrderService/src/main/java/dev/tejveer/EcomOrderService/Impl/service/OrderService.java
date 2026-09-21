package dev.tejveer.EcomOrderService.Impl.service;

import dev.tejveer.EcomOrderService.DTO.OrderListResponseDTO;
import dev.tejveer.EcomOrderService.DTO.OrderRequestDTO;
import dev.tejveer.EcomOrderService.DTO.OrderResponseDTO;
import dev.tejveer.EcomOrderService.DTO.PaymentDTO;
import dev.tejveer.EcomOrderService.DTO.PriceMismatchDto;
import dev.tejveer.EcomOrderService.Exception.CreateOrderException;
import dev.tejveer.EcomOrderService.Exception.OrderListNotFoundException;
import dev.tejveer.EcomOrderService.Exception.OrderNotFoundException;
import dev.tejveer.EcomOrderService.Exception.PriceMismatchException;
import dev.tejveer.EcomOrderService.Exception.UpdateOrderException;
import dev.tejveer.EcomOrderService.Impl.dao.OrderDAO;
import dev.tejveer.EcomOrderService.Impl.dao.PaymentDAO;
import dev.tejveer.EcomOrderService.Model.OrderStatus;
import dev.tejveer.EcomOrderService.Store.OrderStore;
import dev.tejveer.EcomOrderService.Utils.ExecutionService;
import dev.tejveer.EcomOrderService.client.cart.CartFeignClient;
import dev.tejveer.EcomOrderService.client.cart.dto.CartResponse;
import dev.tejveer.EcomOrderService.client.inventory.InventoryFeignClient;
import dev.tejveer.EcomOrderService.client.inventory.dto.InventoryReserveRequest;
import dev.tejveer.EcomOrderService.client.inventory.dto.InventoryReserveResponse;
import dev.tejveer.EcomOrderService.client.product.ProductFeignClient;
import dev.tejveer.EcomOrderService.client.product.dto.ProductBriefDto;
import dev.tejveer.EcomOrderService.client.user.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    private final OrderStore orderStore;
    private final UserFeignClient userFeignClient;
    private final CartFeignClient cartFeignClient;
    private final InventoryFeignClient inventoryFeignClient;
    private final ProductFeignClient productFeignClient;
    private final ExecutionService executionService;

    public OrderService(OrderStore orderStore, UserFeignClient userFeignClient, CartFeignClient cartFeignClient, InventoryFeignClient inventoryFeignClient, ProductFeignClient productFeignClient, ExecutionService executionService) {
        this.orderStore = orderStore;
        this.userFeignClient = userFeignClient;
        this.cartFeignClient = cartFeignClient;
        this.inventoryFeignClient = inventoryFeignClient;
        this.productFeignClient = productFeignClient;
        this.executionService = executionService;
    }

    public OrderResponseDTO createOrder(String userId, OrderRequestDTO orderDTO) throws CreateOrderException {
        InventoryReserveResponse inventory = null;
        try {
            CartResponse cart = cartFeignClient.getCartByUserId(userId);

            UUID orderId = UUID.randomUUID();
            CompletableFuture<InventoryReserveResponse> inventoryReserveResponse = CompletableFuture.supplyAsync(
                    () -> inventoryFeignClient.reserveStock(InventoryReserveRequest.builder()
                            .orderId(orderId)
                            .build()), executionService.orderTaskExecutor());

            CompletableFuture<List<ProductBriefDto>> productBriefDtoList = CompletableFuture.supplyAsync(
                    () -> productFeignClient.getProductBriefs(cart.getItems().stream()
                            .map(item -> item.getProductId())
                            .toList()), executionService.orderTaskExecutor());

            inventory = inventoryReserveResponse.join();
            List<ProductBriefDto> products = productBriefDtoList.join();

            priceMatchCartAndProduct(products, cart);

            OrderDAO orderDAO = OrderHelper.mapFromOrderRequestToOrderDao(userId, orderId, orderDTO.getAddressDto(),
                    products, cart);

            orderStore.storeOrderSummary(orderDAO);
            return OrderResponseDTO.builder()
                    .orderId(orderId.toString())
                    .code(200)
                    .userId(userId)
                    .message("Order has been placed successfully for userId : " + userId)
                    .totalAmount(orderDAO.getTotalAmount())
                    .build();
        } catch (PriceMismatchException pme) {
            releaseInventoryQuietly(inventory);
            throw pme;
        } catch (Exception e) {
            releaseInventoryQuietly(inventory);
            throw new CreateOrderException("Failed to place the order.", e);
        }
    }


    public OrderListResponseDTO getOrderById(String userId, String orderId) throws OrderNotFoundException {
        try {
            return OrderListResponseDTO.builder()
                    .statusCode(200)
                    .userId(userId)
                    .response(orderStore.getOrderListByOrderID(orderId))
                    .build();
        } catch (Exception e) {
            throw new OrderNotFoundException("Failed to fetch the order, orderId : " + orderId, e);
        }
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

    private void releaseInventoryQuietly(InventoryReserveResponse inventory) {
        if (inventory != null && inventory.getReservationId() != null && !inventory.getReservationId().isEmpty()) {
            try {
                inventoryFeignClient.releaseReserveStock(inventory.getReservationId());
            } catch (Exception releaseEx) {
                log.error("Failed to release inventory reservation {}", inventory.getReservationId(), releaseEx);
            }
        }
    }

    private void priceMatchCartAndProduct(List<ProductBriefDto> products, CartResponse cart){
        Map<String, ProductBriefDto> productBriefDtoMap = products.stream()
                .collect(Collectors.toMap(ProductBriefDto::getProductId, Function.identity()));

        List<PriceMismatchDto> mismatches = new ArrayList<>();
        for (CartResponse.CartItemResponse item : cart.getItems()) {
            ProductBriefDto currentProduct = productBriefDtoMap.get(item.getProductId().toString());
            if (!item.getPriceSnapShot().equals(currentProduct.getPrice())) {
                mismatches.add(new PriceMismatchDto(item.getProductId().toString(), item.getPriceSnapShot(), currentProduct.getPrice()));
            }
        }

        if (!mismatches.isEmpty()) {
            throw new PriceMismatchException(mismatches);
        }
    }

}
