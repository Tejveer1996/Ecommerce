package dev.tejveer.Inventory.service;

import dev.tejveer.Inventory.dto.CheckStockRequest;
import dev.tejveer.Inventory.dto.CheckStockResponse;
import dev.tejveer.Inventory.dto.CreateInventoryRequest;
import dev.tejveer.Inventory.dto.DeleteInventoryResponse;
import dev.tejveer.Inventory.dto.InventoryResponse;
import dev.tejveer.Inventory.dto.UpdateInventoryRequest;
import dev.tejveer.Inventory.entity.Inventory;
import dev.tejveer.Inventory.exception.DeleteInventoryException;
import dev.tejveer.Inventory.exception.DuplicateInventoryException;
import dev.tejveer.Inventory.exception.ResourceNotFoundException;
import dev.tejveer.Inventory.exception.UpdateInventoryException;
import dev.tejveer.Inventory.repository.InventoryRepository;
import dev.tejveer.Inventory.repository.InventoryReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public InventoryResponse createInventory(UUID sellerId, CreateInventoryRequest request) throws DuplicateInventoryException {
        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new DuplicateInventoryException(
                    "Inventory already exists for productId: " + request.getProductId());
        }

        Inventory inventory = mapFromCreateInventoryRequest(sellerId, request);

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Created inventory for productId: {}", saved.getProductId());

        return modelMapper.map(saved, InventoryResponse.class);
    }


    public InventoryResponse getInventoryByProductId(UUID productId) throws ResourceNotFoundException {
        Inventory inventory = findInventoryOrThrow(productId);
        return modelMapper.map(inventory, InventoryResponse.class);
    }


    public List<InventoryResponse> getAllInventory(UUID sellerId) {
        return inventoryRepository.findBySellerId(sellerId)
                .stream()
                .map(inventory -> modelMapper.map(inventory, InventoryResponse.class))
                .toList();
    }


    @Transactional
    public DeleteInventoryResponse deleteInventory(UUID sellerId, UUID productId) throws DeleteInventoryException {
        try {
            Inventory inventory = findInventoryOrThrow(productId);
            if (!sellerId.equals(inventory.getSellerId())) {
                throw new IllegalAccessException("Product does not belogs to given seller");
            }
            inventoryRepository.delete(inventory);
            log.info("Deleted inventory for productId: {}", productId);

            return DeleteInventoryResponse.builder()
                    .productId(productId)
                    .deleted(true)
                    .message("Inventory deleted successfully")
                    .build();
        } catch (Exception e) {
            throw new DeleteInventoryException("Failed to delete inventory, error : " + e.getMessage());
        }
    }


    @Transactional
    public InventoryResponse updateInventory(UUID sellerId, UpdateInventoryRequest request) throws UpdateInventoryException {
        try {
            Inventory inventory = findInventoryOrThrow(request.getProductId());

            if (!sellerId.equals(inventory.getSellerId())) {
                throw new IllegalAccessException("Product does not belogs to given seller");
            }
            if (request.getAvailableQuantity() != null) {
                inventory.setAvailableQuantity(request.getAvailableQuantity());
            }
            if (request.getReservedQuantity() != null) {
                inventory.setReservedQuantity(request.getReservedQuantity());
            }
            if (request.getMinimumStock() != null) {
                inventory.setMinimumStock(request.getMinimumStock());
            }
            inventory.setUpdatedAt(Instant.now());

            Inventory updated = inventoryRepository.save(inventory);
            log.info("Updated inventory for productId: {}", updated.getProductId());

            return modelMapper.map(updated, InventoryResponse.class);
        } catch (Exception e) {
            throw new UpdateInventoryException("Failed to update inventory, error :" + e.getMessage());
        }
    }


    public CheckStockResponse checkStock(CheckStockRequest request) throws ResourceNotFoundException {
        Inventory inventory = findInventoryOrThrow(request.getProductId());

        long freeStock = inventory.getAvailableQuantity() - inventory.getReservedQuantity();
        boolean inStock = freeStock >= request.getRequestedQuantity();

        return CheckStockResponse.builder()
                .productId(inventory.getProductId())
                .inStock(inStock)
                .availableQuantity(freeStock)
                .requestedQuantity(request.getRequestedQuantity())
                .build();
    }

    private Inventory findInventoryOrThrow(UUID productId) throws ResourceNotFoundException {
        return inventoryRepository.findByProductId(productId).orElseThrow(
                () -> new ResourceNotFoundException("Inventory not found for productId: " + productId)
        );
    }

    private Inventory mapFromCreateInventoryRequest(UUID sellerId, CreateInventoryRequest request) {
        return Inventory.builder()
                .productId(request.getProductId())
                .availableQuantity(request.getAvailableQuantity() != null ? request.getAvailableQuantity() : 0L)
                .minimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : 0L)
                .reservedQuantity(request.getReservedQuantity() != null ? request.getReservedQuantity() : 0L)
                .sellerId(sellerId)
                .build();
    }
}
