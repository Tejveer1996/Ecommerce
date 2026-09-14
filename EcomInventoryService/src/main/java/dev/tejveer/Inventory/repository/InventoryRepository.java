package dev.tejveer.Inventory.repository;

import dev.tejveer.Inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    boolean existsByProductId(UUID productId);
    Optional<Inventory> findByProductId(UUID productId);
    Optional<Inventory> findBySellerId(UUID sellerId);

    @Modifying
    @Query("UPDATE Inventory i SET i.availableQuantity = i.availableQuantity - :qty, " +
            "i.reservedQuantity = i.reservedQuantity + :qty " +
            "WHERE i.productId = :productId AND i.availableQuantity >= :qty")
    int reserveStock(@Param("productId") UUID productId, @Param("qty") Long qty);

    @Modifying
    @Query("UPDATE Inventory i SET i.availableQuantity = i.availableQuantity + :qty, " +
            "i.reservedQuantity = i.reservedQuantity - :qty " +
            "WHERE i.productId = :productId AND i.reservedQuantity >= :qty")
    int releaseStock(@Param("productId") UUID productId, @Param("qty") Long qty);

    @Modifying
    @Query("UPDATE Inventory i SET i.reservedQuantity = i.reservedQuantity - :qty " +
            "WHERE i.productId = :productId AND i.reservedQuantity >= :qty")
    int confirmStock(@Param("productId") UUID productId, @Param("qty") Long qty);
}
