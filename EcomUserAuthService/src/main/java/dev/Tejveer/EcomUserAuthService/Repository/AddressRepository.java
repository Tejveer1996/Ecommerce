package dev.Tejveer.EcomUserAuthService.Repository;

import dev.Tejveer.EcomUserAuthService.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserId(UUID userId);
}
