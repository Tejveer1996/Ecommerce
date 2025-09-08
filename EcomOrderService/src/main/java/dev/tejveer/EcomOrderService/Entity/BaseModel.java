package dev.tejveer.EcomOrderService.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@MappedSuperclass
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;
    boolean isActive;
    @CreationTimestamp
    String createdAt;
    @UpdateTimestamp
    String updatedAt;
}
