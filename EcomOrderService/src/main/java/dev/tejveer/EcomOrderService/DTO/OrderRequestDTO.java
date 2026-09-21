package dev.tejveer.EcomOrderService.DTO;

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
public class OrderRequestDTO {
    AddressDto addressDto;

    @Data
    @Builder
    public class AddressDto {
        String id;
        String address;
        String city;
        String state;
        String country;
        String postalCode;
        String addressType;
        boolean isDefault;
    }
}
