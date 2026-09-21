package dev.Tejveer.EcomUserAuthService.Service;

import dev.Tejveer.EcomUserAuthService.DTO.AddressResponseDTO;
import dev.Tejveer.EcomUserAuthService.Entity.Address;
import dev.Tejveer.EcomUserAuthService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomUserAuthService.Repository.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ModelMapper modelMapper;

    public AddressResponseDTO getAddressById(UUID addressId, UUID userId) throws ResourceNotFoundException {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));

        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found: " + addressId);
        }

        return modelMapper.map(address, AddressResponseDTO.class);
    }
}
