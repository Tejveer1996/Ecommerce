package dev.Tejveer.EcomUserAuthService.Service.Implementation;


import dev.Tejveer.EcomUserAuthService.DTO.AddressResponseDTO;
import dev.Tejveer.EcomUserAuthService.Entity.Address;
import dev.Tejveer.EcomUserAuthService.Exception.AddressNotFoundException;
import dev.Tejveer.EcomUserAuthService.Repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public AddressResponseDTO getAddressById(UUID addressId, UUID userId) throws AddressNotFoundException {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found: " + addressId));

        if (!address.getUser().getId().equals(userId)) {
            throw new AddressNotFoundException("Address not found: " + addressId);
        }

        return modelMapper.map(address, AddressResponseDTO.class);
    }
}
