package dev.Tejveer.EcomUserAuthService.Repository;

import dev.Tejveer.EcomUserAuthService.Entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SellerRepository extends JpaRepository<SellerProfile, UUID> {
}
