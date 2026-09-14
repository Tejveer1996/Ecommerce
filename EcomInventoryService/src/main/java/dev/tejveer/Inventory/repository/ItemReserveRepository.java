package dev.tejveer.Inventory.repository;

import dev.tejveer.Inventory.entity.ItemReserve;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemReserveRepository extends JpaRepository<ItemReserve, UUID> {
}
