package dev.tejveer.Inventory.service;


import dev.tejveer.Inventory.dto.ConfirmReserveStockRequest;
import dev.tejveer.Inventory.dto.ReleaseReserveStockRequest;
import dev.tejveer.Inventory.dto.ReservationBatchActionResponse;
import dev.tejveer.Inventory.dto.ReservationResponse;
import dev.tejveer.Inventory.dto.ReserveItemsRequest;
import dev.tejveer.Inventory.exception.InventoryReservationException;

public interface InventoryReservationService {

    ReservationResponse reserveStock(ReserveItemsRequest request) throws InventoryReservationException;

    ReservationBatchActionResponse releaseReserveStock(ReleaseReserveStockRequest request) throws InventoryReservationException;

    ReservationBatchActionResponse expireReserveStock(ReleaseReserveStockRequest request) throws InventoryReservationException;

    ReservationBatchActionResponse confirmReserveStock(ConfirmReserveStockRequest request) throws InventoryReservationException;
}
