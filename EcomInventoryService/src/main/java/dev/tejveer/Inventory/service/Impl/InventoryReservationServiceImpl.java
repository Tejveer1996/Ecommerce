package dev.tejveer.Inventory.service.Impl;

import dev.tejveer.Inventory.dto.ConfirmReserveStockRequest;
import dev.tejveer.Inventory.dto.ReleaseReserveStockRequest;
import dev.tejveer.Inventory.dto.ReservationBatchActionResponse;
import dev.tejveer.Inventory.dto.ReservationResponse;
import dev.tejveer.Inventory.dto.ReserveItemsRequest;
import dev.tejveer.Inventory.entity.InventoryReservation;
import dev.tejveer.Inventory.entity.enums.ReservationStatus;
import dev.tejveer.Inventory.exception.InventoryReservationException;
import dev.tejveer.Inventory.exception.ResourceNotFoundException;
import dev.tejveer.Inventory.repository.InventoryReservationRepository;
import dev.tejveer.Inventory.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationServiceImpl implements InventoryReservationService {
    InventoryReservationRepository reservationRepository;
    ModelMapper mapper;

    @Override
    public ReservationResponse reserveStock(ReserveItemsRequest request) throws InventoryReservationException {
        try {
            if (reservationRepository.existsByOrderId(request.getOrderId())) {
                throw new IllegalArgumentException("Order id already exist");
            }

            List<InventoryReservation> newReservations = new ArrayList<>();
            for (ReserveItemsRequest.Item item : request.getItems()) {
                InventoryReservation inventoryReservation = InventoryReservation.builder()
                        .orderId(request.getOrderId())
                        .sellerId(item.getSellerId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .status(ReservationStatus.RESERVED)
                        .expiresAt(Instant.now().plus(Duration.ofMinutes(15)))
                        .build();
                newReservations.add(inventoryReservation);
            }
            List<InventoryReservation> reservedItemsList = reservationRepository.saveAll(newReservations);
            ReservationResponse response = ReservationResponse.builder()
                    .orderId(request.getOrderId()).reservedItemList(new ArrayList<>()).build();
            reservedItemsList.stream().map(resInv -> response.getReservedItemList().add(
                    ReservationResponse.ReservedItems.builder()
                            .reservationId(resInv.getId())
                            .status(resInv.getStatus())
                            .sellerId(resInv.getSellerId())
                            .productId(resInv.getProductId())
                            .quantity(resInv.getQuantity())
                            .build()
            ));
            return response;
        } catch (Exception e) {
            throw new InventoryReservationException("failed to reserve, error : " + e.getMessage());
        }
    }

    @Override
    public ReservationBatchActionResponse releaseReserveStock(ReleaseReserveStockRequest request) throws InventoryReservationException {
        try {
            List<InventoryReservation> inventoryReservationList =
                    reservationRepository.findByOrderId(request.getOrderId()).orElseThrow(
                            () -> new ResourceNotFoundException("Order id does not exist")
                    );
            for (InventoryReservation inventoryReservation : inventoryReservationList) {
                inventoryReservation.setStatus(ReservationStatus.RELEASED);
            }
            reservationRepository.saveAll(inventoryReservationList);

            return ReservationBatchActionResponse.builder()
                    .orderId(request.getOrderId())
                    .reservationIds(inventoryReservationList.stream().map(
                            reservation -> reservation.getId()).collect(Collectors.toList()))
                    .itemsProcessed(inventoryReservationList.size())
                    .resultingStatus(ReservationStatus.RELEASED)
                    .message("All the above reserved items has been released")
                    .build();
        } catch (Exception e) {
            throw new InventoryReservationException("failed to release stock, error : " + e.getMessage());
        }
    }

    @Override
    public ReservationBatchActionResponse confirmReserveStock(ConfirmReserveStockRequest request) throws InventoryReservationException {
        try {
            List<InventoryReservation> inventoryReservationList =
                    reservationRepository.findByOrderId(request.getOrderId()).orElseThrow(
                            () -> new ResourceNotFoundException("Order id does not exist")
                    );
            for (InventoryReservation inventoryReservation : inventoryReservationList) {
                inventoryReservation.setStatus(ReservationStatus.CONFIRMED);
            }
            reservationRepository.saveAll(inventoryReservationList);

            return ReservationBatchActionResponse.builder()
                    .orderId(request.getOrderId())
                    .reservationIds(inventoryReservationList.stream().map(
                            reservation -> reservation.getId()).collect(Collectors.toList()))
                    .itemsProcessed(inventoryReservationList.size())
                    .resultingStatus(ReservationStatus.RELEASED)
                    .message("All the above reserved items has been released")
                    .build();
        } catch (Exception e) {
            throw new InventoryReservationException("failed to Confirm stock, error : " + e.getMessage());
        }
    }

    @Override
    public ReservationBatchActionResponse expireReserveStock(ReleaseReserveStockRequest request) throws InventoryReservationException {
        try {
            List<InventoryReservation> inventoryReservationList =
                    reservationRepository.findByOrderId(request.getOrderId()).orElseThrow(
                            () -> new ResourceNotFoundException("Order id does not exist")
                    );
            for (InventoryReservation inventoryReservation : inventoryReservationList) {
                if (inventoryReservation.getExpiresAt().isBefore(Instant.now())){
                    throw new IllegalStateException("Yet to expire");
                }
                inventoryReservation.setStatus(ReservationStatus.EXPIRED);
            }
            reservationRepository.saveAll(inventoryReservationList);

            return ReservationBatchActionResponse.builder()
                    .orderId(request.getOrderId())
                    .reservationIds(inventoryReservationList.stream().map(
                            reservation -> reservation.getId()).collect(Collectors.toList()))
                    .itemsProcessed(inventoryReservationList.size())
                    .resultingStatus(ReservationStatus.EXPIRED)
                    .message("All the above reserved items has been released")
                    .build();
        } catch (Exception e) {
            throw new InventoryReservationException("failed to Confirm stock, error : " + e.getMessage());
        }
    }

}
