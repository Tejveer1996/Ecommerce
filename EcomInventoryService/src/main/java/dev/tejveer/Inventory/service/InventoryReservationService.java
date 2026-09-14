package dev.tejveer.Inventory.service;

import dev.tejveer.Inventory.dto.ConfirmReserveStockRequest;
import dev.tejveer.Inventory.dto.ReleaseReserveStockRequest;
import dev.tejveer.Inventory.dto.ReservationActionResponse;
import dev.tejveer.Inventory.dto.ReservationResponse;
import dev.tejveer.Inventory.dto.ReserveItemsRequest;
import dev.tejveer.Inventory.entity.InventoryReservation;
import dev.tejveer.Inventory.entity.ItemReserve;
import dev.tejveer.Inventory.entity.enums.ReservationStatus;
import dev.tejveer.Inventory.exception.InventoryReservationException;
import dev.tejveer.Inventory.exception.ResourceNotFoundException;
import dev.tejveer.Inventory.repository.InventoryRepository;
import dev.tejveer.Inventory.repository.InventoryReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationService {
    private final InventoryReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Reserve stock when order is placed. Do the stock check and update in one query to maintain
     * concurrency(prevent race condition)
     */
    @Transactional(rollbackFor = InventoryReservationException.class)
    public ReservationResponse reserveStock(ReserveItemsRequest request) throws InventoryReservationException {
        try {
            if (reservationRepository.existsByOrderId(request.getOrderId())) {
                throw new IllegalArgumentException("Order id already exist");
            }
            InventoryReservation reservation = InventoryReservation.builder()
                    .orderId(request.getOrderId())
                    .status(ReservationStatus.RESERVED)
                    .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                    .build();
            List<ItemReserve> itemList = new ArrayList<>();
            for (ReserveItemsRequest.Item item : request.getItems()) {
                int reserveRow = inventoryRepository.reserveStock(item.getProductId(), item.getQuantity());
                if (reserveRow > 0) {
                    ItemReserve itemReserve = ItemReserve.builder()
                            .reservation(reservation)
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .build();
                    itemList.add(itemReserve);
                } else {
                    throw new InventoryReservationException("Stock not available for item : " + item.getProductId());
                }
            }
            reservation.setItemReserveList(itemList);
            InventoryReservation reservationResponse = reservationRepository.save(reservation);

            return ReservationResponse.builder()
                    .orderId(reservation.getOrderId().toString())
                    .reservationId(reservationResponse.getId().toString())
                    .reservedItemIds(reservationResponse.getItemReserveList().stream()
                            .map(itemReserve -> itemReserve.getProductId().toString())
                            .collect(Collectors.toList()))
                    .build();
        } catch (Exception e) {
            throw new InventoryReservationException("failed to reserve, error : " + e.getMessage());
        }
    }


    /**
     *Release the stock when the customer or system cancel the order.
     */
    @Transactional(rollbackFor = InventoryReservationException.class)
    public ReservationActionResponse releaseReserveStock(ReleaseReserveStockRequest request) throws InventoryReservationException {
        try {
            InventoryReservation reservation = reservationRepository.findByOrderId(request.getOrderId()).orElseThrow(
                    () -> new ResourceNotFoundException("Given order does not exist")
            );
            int count =0;
            if(reservation.getStatus() == ReservationStatus.RESERVED){
                for(ItemReserve itemReserve : reservation.getItemReserveList()) {
                   inventoryRepository.releaseStock(itemReserve.getProductId(), itemReserve.getQuantity());
                   count++;
                }
                reservation.setStatus(ReservationStatus.RELEASED);
            }
            return ReservationActionResponse.builder()
                    .reservationId(reservation.getId().toString())
                    .itemsProcessed(count)
                    .orderId(reservation.getOrderId().toString())
                    .status(reservation.getStatus().name())
                    .message(count>0 ? "Reserve items has been released" : "Given order has been already : "
                            +reservation.getStatus().name())
                    .build();

        } catch (Exception e) {
            throw new InventoryReservationException("failed to release stock, error : " + e.getMessage());
        }
    }


    /**
     * Confirm the status of the stock, when the payment get successfull, to maintain the idempotency
     * of webhook it first checks the status for update the status.
     */
    @Transactional(rollbackFor = InventoryReservationException.class)
    public ReservationActionResponse confirmReserveStock(ConfirmReserveStockRequest request) throws InventoryReservationException {
        try {
            InventoryReservation reservation = reservationRepository.findByOrderId(request.getOrderId()).orElseThrow(
                    () -> new ResourceNotFoundException("Given order does not exist")
            );
            int count =0;
            if(reservation.getStatus() == ReservationStatus.RESERVED){
                for(ItemReserve itemReserve : reservation.getItemReserveList()) {
                    inventoryRepository.confirmStock(itemReserve.getProductId(), itemReserve.getQuantity());
                    count++;
                }
                reservation.setStatus(ReservationStatus.CONFIRMED);
            }
            return ReservationActionResponse.builder()
                    .reservationId(reservation.getId().toString())
                    .itemsProcessed(count)
                    .orderId(reservation.getOrderId().toString())
                    .status(reservation.getStatus().name())
                    .message(count>0 ? "Reserve items has been confirmed" : "Given order has been already : "
                            +reservation.getStatus().name())
                    .build();

        } catch (Exception e) {
            throw new InventoryReservationException("failed to confirm stock, error : " + e.getMessage());
        }
    }


    /**
     * This update the status of the reserve expired, in case of when reservation expired either though
     * payment failure or any condition happen.
     */
    @Transactional(rollbackFor = InventoryReservationException.class)
    public ReservationActionResponse expireReserveStock(ReleaseReserveStockRequest request) throws InventoryReservationException {
        try {
            InventoryReservation reservation = reservationRepository.findByOrderId(request.getOrderId()).orElseThrow(
                    () -> new ResourceNotFoundException("Given order does not exist")
            );
            int count =0;
            if (reservation.getStatus() == ReservationStatus.RESERVED && reservation.getExpiresAt().isBefore(Instant.now())) {
                for(ItemReserve itemReserve : reservation.getItemReserveList()) {
                   inventoryRepository.releaseStock(itemReserve.getProductId(), itemReserve.getQuantity());
                    count++;
                }
                reservation.setStatus(ReservationStatus.EXPIRED);
            }
            return ReservationActionResponse.builder()
                    .reservationId(reservation.getId().toString())
                    .itemsProcessed(count)
                    .orderId(reservation.getOrderId().toString())
                    .status(reservation.getStatus().name())
                    .message(count>0 ? "Reserve items has been expired" : "Given order has been already : "
                            +reservation.getStatus().name())
                    .build();

        }  catch (Exception e) {
            throw new InventoryReservationException("failed to expire stock, error : " + e.getMessage());
        }
    }

}
