package dev.tejveer.EcomCartService.repository;

import dev.tejveer.EcomCartService.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    boolean existsByUserId(UUID userId);
    Cart findByUserId(UUID userId);
}
