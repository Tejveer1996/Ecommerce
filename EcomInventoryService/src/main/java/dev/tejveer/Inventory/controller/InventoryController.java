package dev.tejveer.Inventory.controller;

import dev.tejveer.Inventory.dto.CheckStockRequest;
import dev.tejveer.Inventory.dto.CheckStockResponse;
import dev.tejveer.Inventory.dto.CreateInventoryRequest;
import dev.tejveer.Inventory.dto.DeleteInventoryResponse;
import dev.tejveer.Inventory.dto.InventoryResponse;
import dev.tejveer.Inventory.dto.ReservationResponse;
import dev.tejveer.Inventory.dto.ReserveStockListRequest;
import dev.tejveer.Inventory.dto.UpdateInventoryRequest;
import dev.tejveer.Inventory.exception.DuplicateInventoryException;
import dev.tejveer.Inventory.exception.ResourceNotFoundException;
import dev.tejveer.Inventory.service.InventoryService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Inventory APIs",
        description = "Operations related to seller product inventory"
)
@RestController
@Slf4j
@RequestMapping("/apis/inventory")
public class InventoryController {

    private static final String ERROR_MESSAGE = "Something went wrong";

    @Autowired
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Create inventory", description = "Create a new inventory row for a seller's product (Seller only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory Created"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Create Inventory", content = @Content),
            @ApiResponse(responseCode = "409", description = "Inventory Already Exists For This Product", content = @Content)
    })
    @PostMapping("/create")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody CreateInventoryRequest request) {
        try {
            String sellerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            InventoryResponse response = inventoryService.createInventory(UUID.fromString(sellerId), request);
            return ResponseEntity.ok(response);
        } catch (DuplicateInventoryException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while creating inventory , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Update inventory", description = "Update stock details of an existing inventory row (Seller only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory Updated"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Update Inventory", content = @Content),
            @ApiResponse(responseCode = "404", description = "Inventory Not Found", content = @Content)
    })
    @PutMapping("/update")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<InventoryResponse> updateInventory(@RequestBody UpdateInventoryRequest request) {
        try {
            String sellerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            InventoryResponse response = inventoryService.updateInventory(UUID.fromString(sellerId), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while updating inventory , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Delete inventory", description = "Delete an inventory row by product id (Seller only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory Deleted"),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Delete Inventory", content = @Content),
            @ApiResponse(responseCode = "404", description = "Inventory Not Found", content = @Content)
    })
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<DeleteInventoryResponse> deleteInventory(@PathVariable UUID productId) {
        try {
            String sellerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            DeleteInventoryResponse response = inventoryService.deleteInventory(UUID.fromString(sellerId), productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while deleting inventory, productId :{}, error :: {}", productId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Get inventory by product id", description = "Fetch a single inventory row's details using its product id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory Found"),
            @ApiResponse(responseCode = "404", description = "Inventory Not Found", content = @Content)
    })
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable UUID productId) {
        try {
            InventoryResponse response = inventoryService.getInventoryByProductId(productId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while getting inventory, productId :{}, error :: {}", productId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Get all inventory", description = "Fetch all inventory rows across all sellers (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All Inventory Fetched Successfully"),
            @ApiResponse(responseCode = "403", description = "Not Authorized To View All Inventory", content = @Content)
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        try {
            String sellerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<InventoryResponse> response = inventoryService.getAllInventory(UUID.fromString(sellerId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting all inventory, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Check stock", description = "Check whether the requested quantity is available in stock for a product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock Check Completed"),
            @ApiResponse(responseCode = "404", description = "Inventory Not Found", content = @Content)
    })
    @GetMapping("/check-stock")
    public ResponseEntity<CheckStockResponse> checkStock(@RequestBody CheckStockRequest request) {
        try {
            CheckStockResponse response = inventoryService.checkStock(request);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while checking stock, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Reserve stock", description = "Check whether the requested quantity is available in stock for a product," +
            "and reserve the stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock Check Completed"),
            @ApiResponse(responseCode = "404", description = "Inventory Not Found", content = @Content),

    })
    @GetMapping("/reserve-stock")
    public ResponseEntity<ReservationResponse> reserveStock(@RequestBody ReserveStockListRequest request) {
        try {
            ReservationResponse response = inventoryService.reserveStock(request);
            return ResponseEntity.ok(response);
//        } catch (ResourceNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while checking stock, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }
}
