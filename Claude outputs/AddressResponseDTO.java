package dev.Tejveer.EcomUserAuthService.DTO;

import dev.Tejveer.EcomUserAuthService.Entity.AddressType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponseDTO {
    UUID id;
    String address;
    String city;
    String state;
    String country;
    String postalCode;
    AddressType addressType;
    boolean isDefault;
}
