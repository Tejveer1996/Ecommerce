package dev.tejveer.Inventory.service;

import dev.tejveer.Inventory.dto.CheckStockRequest;
import dev.tejveer.Inventory.dto.CheckStockResponse;
import dev.tejveer.Inventory.dto.CreateInventoryRequest;
import dev.tejveer.Inventory.dto.DeleteInventoryResponse;
import dev.tejveer.Inventory.dto.InventoryResponse;
import dev.tejveer.Inventory.dto.UpdateInventoryRequest;
import dev.tejveer.Inventory.exception.DeleteInventoryException;
import dev.tejveer.Inventory.exception.DuplicateInventoryException;
import dev.tejveer.Inventory.exception.ResourceNotFoundException;
import dev.tejveer.Inventory.exception.UpdateInventoryException;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    InventoryResponse createInventory(UUID sellerId, CreateInventoryRequest request) throws DuplicateInventoryException;

    InventoryResponse getInventoryByProductId(UUID productId) throws ResourceNotFoundException;

    List<InventoryResponse> getAllInventory(UUID sellerId);

    DeleteInventoryResponse deleteInventory(UUID sellerId, UUID productId) throws DeleteInventoryException;

    InventoryResponse updateInventory(UUID sellerId, UpdateInventoryRequest request) throws UpdateInventoryException;

    CheckStockResponse checkStock(CheckStockRequest request) throws ResourceNotFoundException;
}
