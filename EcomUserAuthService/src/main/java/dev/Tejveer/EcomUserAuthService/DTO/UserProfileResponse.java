package dev.Tejveer.EcomUserAuthService.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.Tejveer.EcomUserAuthService.Entity.Address;
import dev.Tejveer.EcomUserAuthService.Entity.Roles;
import dev.Tejveer.EcomUserAuthService.Entity.SellerProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    String email;
    String name;
    String phoneNumber;
    List<Roles> roles;
    String profilePictureUrl;
    String createdAt;

    List<AddressResponseDTO> addresses = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    SellerProfileResponse sellerProfile;
}
