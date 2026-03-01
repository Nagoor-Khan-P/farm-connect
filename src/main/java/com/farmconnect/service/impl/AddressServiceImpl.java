package com.farmconnect.service.impl;

import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.model.Address;
import com.farmconnect.model.SavedAddress;
import com.farmconnect.model.User;
import com.farmconnect.payload.request.AddressRequest;
import com.farmconnect.payload.response.AddressResponse;
import com.farmconnect.repository.AddressRepository;
import com.farmconnect.repository.UserRepository;
import com.farmconnect.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AddressResponse addAddress(UUID userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.isDefault()) {
            resetDefaultAddresses(userId);
        }

        SavedAddress savedAddress = SavedAddress.builder()
                .user(user)
                .label(request.getLabel())
                .address(mapToAddress(request))
                .isDefault(request.isDefault())
                .build();

        return mapToResponse(addressRepository.save(savedAddress));
    }

    @Override
    public List<AddressResponse> getUserAddresses(UUID userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request) {
        SavedAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this address");
        }

        if (request.isDefault()) {
            resetDefaultAddresses(userId);
        }

        address.setLabel(request.getLabel());
        address.setAddress(mapToAddress(request));
        address.setDefault(request.isDefault());

        return mapToResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        SavedAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this address");
        }

        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(UUID userId, UUID addressId) {
        resetDefaultAddresses(userId);
        SavedAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to set this address as default");
        }

        address.setDefault(true);
        return mapToResponse(addressRepository.save(address));
    }

    private void resetDefaultAddresses(UUID userId) {
        List<SavedAddress> addresses = addressRepository.findByUserId(userId);
        addresses.forEach(a -> a.setDefault(false));
        addressRepository.saveAll(addresses);
    }

    private Address mapToAddress(AddressRequest request) {
        return Address.builder()
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .build();
    }

    private AddressResponse mapToResponse(SavedAddress savedAddress) {
        AddressResponse response = new AddressResponse();
        response.setId(savedAddress.getId());
        response.setLabel(savedAddress.getLabel());
        response.setStreet(savedAddress.getAddress().getStreet());
        response.setCity(savedAddress.getAddress().getCity());
        response.setState(savedAddress.getAddress().getState());
        response.setZipCode(savedAddress.getAddress().getZipCode());
        response.setCountry(savedAddress.getAddress().getCountry());
        response.setDefault(savedAddress.isDefault());
        return response;
    }
}
