package dev.tejveer.Inventory.controller;

import dev.tejveer.Inventory.dto.ConfirmReserveStockRequest;
import dev.tejveer.Inventory.dto.ReleaseReserveStockRequest;
import dev.tejveer.Inventory.dto.ReservationBatchActionResponse;
import dev.tejveer.Inventory.dto.ReservationResponse;
import dev.tejveer.Inventory.dto.ReserveItemsRequest;
import dev.tejveer.Inventory.exception.InventoryReservationException;
import dev.tejveer.Inventory.service.InventoryReservationService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(
        name = "Inventory Reservation APIs",
        description = "Operations related to reserving, confirming, releasing, and expiring stock reservations for orders"
)
@RestController
@Slf4j
@RequestMapping("/apis/inventory/reservation")
public class InventoryReservationController {

    private static final String ERROR_MESSAGE = "Something went wrong";

    @Autowired
    private final InventoryReservationService inventoryReservationService;

    public InventoryReservationController(InventoryReservationService inventoryReservationService) {
        this.inventoryReservationService = inventoryReservationService;
    }

    @Operation(summary = "Reserve stock", description = "Reserve stock for the given items against an order (called by Order Service)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock Reserved"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "409", description = "Insufficient Stock / Reservation Conflict", content = @Content),
            @ApiResponse(responseCode = "404", description = "Inventory Not Found For Requested Item", content = @Content)
    })
    @PostMapping("/reserve")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<ReservationResponse> reserveStock(@RequestBody ReserveItemsRequest request) {
        try {
            ReservationResponse response = inventoryReservationService.reserveStock(request);
            return ResponseEntity.ok(response);
        } catch (InventoryReservationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while reserving stock , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Release reserved stock", description = "Release all reservations tied to an order, returning stock to the available pool (e.g. payment failed/cancelled)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservations Released"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reservations Not Found For Order", content = @Content)
    })
    @PostMapping("/release")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<ReservationBatchActionResponse> releaseReserveStock(@RequestBody ReleaseReserveStockRequest request) {
        try {
            ReservationBatchActionResponse response = inventoryReservationService.releaseReserveStock(request);
            return ResponseEntity.ok(response);
        } catch (InventoryReservationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while releasing reserved stock , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Expire reserved stock", description = "Mark all reservations tied to an order as expired and return stock to the available pool (e.g. checkout abandoned past TTL)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservations Expired"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reservations Not Found For Order", content = @Content)
    })
    @PostMapping("/expire")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<ReservationBatchActionResponse> expireReserveStock(@RequestBody ReleaseReserveStockRequest request) {
        try {
            ReservationBatchActionResponse response = inventoryReservationService.expireReserveStock(request);
            return ResponseEntity.ok(response);
        } catch (InventoryReservationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while expiring reserved stock , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Confirm reserved stock", description = "Confirm all reservations tied to an order, permanently deducting stock (e.g. payment succeeded)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservations Confirmed"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reservations Not Found For Order", content = @Content)
    })
    @PostMapping("/confirm")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<ReservationBatchActionResponse> confirmReserveStock(@RequestBody ConfirmReserveStockRequest request) {
        try {
            ReservationBatchActionResponse response = inventoryReservationService.confirmReserveStock(request);
            return ResponseEntity.ok(response);
        } catch (InventoryReservationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while confirming reserved stock , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }
}
