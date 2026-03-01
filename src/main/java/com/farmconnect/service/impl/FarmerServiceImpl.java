package com.farmconnect.service.impl;

import com.farmconnect.exception.ResourceNotFoundException;
import com.farmconnect.model.Role;
import com.farmconnect.model.User;
import com.farmconnect.payload.response.FarmerResponse;
import com.farmconnect.repository.UserRepository;
import com.farmconnect.service.FarmerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FarmerServiceImpl implements FarmerService {

    private final UserRepository userRepository;

    public FarmerServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<FarmerResponse> getAllFarmers() {
        return userRepository.findByRole(Role.ROLE_FARMER).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FarmerResponse getFarmerById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        if (user.getRole() != Role.ROLE_FARMER) {
            throw new ResourceNotFoundException("User is not a farmer");
        }

        return mapToResponse(user);
    }

    private FarmerResponse mapToResponse(User user) {
        return FarmerResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
