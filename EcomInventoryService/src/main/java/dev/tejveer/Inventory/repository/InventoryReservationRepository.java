package dev.tejveer.Inventory.repository;

import dev.tejveer.Inventory.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {
    boolean existsByOrderId(UUID orderId);
   Optional<InventoryReservation> findByOrderId(UUID orderId);
}
