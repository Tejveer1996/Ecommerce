package dev.tejveer.EcomCartService.repository;

import dev.tejveer.EcomCartService.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}
