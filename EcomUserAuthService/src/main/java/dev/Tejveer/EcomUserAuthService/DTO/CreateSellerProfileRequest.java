package dev.Tejveer.EcomUserAuthService.DTO;

import dev.Tejveer.EcomUserAuthService.Entity.Address;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateSellerProfileRequest {
    @NotNull
    String storeName;

    @NotNull
    Address storeAddress;

    @NotNull
    String storeDescription;

    @NotNull
    String gst;
}
