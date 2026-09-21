package dev.Tejveer.EcomUserAuthService.Controller;


import dev.Tejveer.EcomUserAuthService.DTO.AddressResponseDTO;
import dev.Tejveer.EcomUserAuthService.Exception.AddressNotFoundException;
import dev.Tejveer.EcomUserAuthService.Service.Implementation.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Address APIs",
        description = "Operations related to fetching a user's saved addresses"
)
@RestController
@RequestMapping("apis/user/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "Get address by id", description = "Fetch a single saved address belonging to the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address Fetched Successfully"),
            @ApiResponse(responseCode = "404", description = "Address Not Found", content = @Content)
    })
    @GetMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable UUID addressId) throws AddressNotFoundException {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AddressResponseDTO response = addressService.getAddressById(addressId, UUID.fromString(userId));
        return ResponseEntity.ok(response);
    }
}
