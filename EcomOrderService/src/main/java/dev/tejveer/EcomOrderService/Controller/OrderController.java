package dev.tejveer.EcomOrderService.Controller;

import dev.tejveer.EcomOrderService.DTO.OrderListResponseDTO;
import dev.tejveer.EcomOrderService.DTO.OrderRequestDTO;
import dev.tejveer.EcomOrderService.DTO.OrderResponseDTO;
import dev.tejveer.EcomOrderService.DTO.PaymentDTO;
import dev.tejveer.EcomOrderService.Service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/apis/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/placeorder")
    public OrderResponseDTO placeOrder(@RequestBody OrderRequestDTO orderRequestDTO) throws Exception {
        try {
            return orderService.createOrder(orderRequestDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while placing the order" +
                    "for userId : " + orderRequestDTO.getUserId());
        }
    }

    @GetMapping("/orderlist")
    public OrderListResponseDTO getOrderList(@RequestParam String userId) {
        try {
            return orderService.getOrdersByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User Id not found");
        }
    }

    /*
    This API is called by payment service, after getting response from any payment entity.
     */
    @PostMapping("/update/paymentstatus/{userId}")
    public ResponseEntity updatePaymentStatus(@PathVariable String userId, @RequestBody PaymentDTO paymentDTO) {
        try {
            orderService.updateOrderPaymentStatus(userId, paymentDTO);
            return ResponseEntity.ok("Payment status has been updated successfully");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update payment status");
        }
    }
}
