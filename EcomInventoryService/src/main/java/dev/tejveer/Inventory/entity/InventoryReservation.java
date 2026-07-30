package dev.tejveer.Inventory.entity;

import dev.tejveer.Inventory.entity.enums.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventory_reservation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "seller_id", nullable = false)
    UUID sellerId;

    @Column(name = "product_id", nullable = false)
    UUID productId;

    @Column(name = "order_id", nullable = false)
    UUID orderId;

    @Column(nullable = false)
    Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    ReservationStatus status = ReservationStatus.RESERVED;

    @Column(name = "expires_at")
    Instant expiresAt;

    @CreationTimestamp
    @Column(updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
}
