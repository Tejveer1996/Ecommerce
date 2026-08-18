package dev.tejveer.EcomOrderService.Controller;

import dev.tejveer.EcomOrderService.DTO.OrderListResponseDTO;
import dev.tejveer.EcomOrderService.DTO.OrderRequestDTO;
import dev.tejveer.EcomOrderService.DTO.OrderResponseDTO;
import dev.tejveer.EcomOrderService.DTO.PaymentDTO;
import dev.tejveer.EcomOrderService.Interface.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(
        name = "Order APIs",
        description = "Operations related to placing orders, viewing order history, and updating payment status"
)
@RestController
@Slf4j
@RequestMapping("/apis/order")
public class OrderController {

    private static final String ERROR_MESSAGE = "Something went wrong";

    @Autowired
    private IOrderService orderService;

    @Operation(summary = "Place order", description = "Create a new order for the logged-in user from their cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order Placed Successfully"),
            @ApiResponse(responseCode = "400", description = "Order Placement Failed / Validation Error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing Or Invalid Authentication Token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Place Order", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/placeorder")
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDTO placeOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        try {
            String userId = extractUserId();
            orderRequestDTO.setUserId(userId);
            return orderService.createOrder(orderRequestDTO);
        } catch (Exception e) {
            log.error("Error occurred while placing order for userId : {}, error :: {}",
                    orderRequestDTO.getUserId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error occurred while placing the order for userId : " + orderRequestDTO.getUserId());
        }
    }

    @Operation(summary = "Get order list", description = "Fetch all orders placed by the logged-in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order List Fetched Successfully"),
            @ApiResponse(responseCode = "401", description = "Missing Or Invalid Authentication Token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To View Orders", content = @Content),
            @ApiResponse(responseCode = "404", description = "User Id Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/orderlist")
    @PreAuthorize("hasRole('USER')")
    public OrderListResponseDTO getOrderList() {
        try {
            String userId = extractUserId();
            return orderService.getOrdersByUserId(userId);
        } catch (Exception e) {
            log.error("Error occurred while fetching order list, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User Id not found");
        }
    }

    @Operation(
            summary = "Update payment status",
            description = "Internal callback invoked by the Payment Service after receiving a response from the payment provider, to update the order's payment status. Not intended for direct client use."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment Status Updated Successfully"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order Not Found For User", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/update/paymentstatus/{userId}")
    public ResponseEntity<String> updatePaymentStatus(@PathVariable String userId, @RequestBody PaymentDTO paymentDTO) {
        try {
            orderService.updateOrderPaymentStatus(userId, paymentDTO);
            return ResponseEntity.ok("Payment status has been updated successfully");
        } catch (Exception e) {
            log.error("Error occurred while updating payment status for userId : {}, error :: {}", userId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update payment status");
        }
    }

    private String extractUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
