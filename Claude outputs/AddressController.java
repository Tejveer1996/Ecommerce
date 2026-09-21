package dev.Tejveer.EcomUserAuthService.Controller;

import dev.Tejveer.EcomUserAuthService.DTO.AddressResponseDTO;
import dev.Tejveer.EcomUserAuthService.Service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/apis/user/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponseDTO> getAddressById(
            @PathVariable UUID addressId,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        AddressResponseDTO response = addressService.getAddressById(addressId, userId);
        return ResponseEntity.ok(response);
    }
}
