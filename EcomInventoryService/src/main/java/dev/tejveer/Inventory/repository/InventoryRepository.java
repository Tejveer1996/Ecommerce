package dev.tejveer.Inventory.repository;

import dev.tejveer.Inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    boolean existsByProductId(UUID productId);
    Optional<Inventory> findByProductId(UUID productId);
    Optional<Inventory> findBySellerId(UUID sellerId);

}
