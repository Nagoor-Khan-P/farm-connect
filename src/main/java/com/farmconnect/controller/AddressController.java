package com.farmconnect.controller;

import com.farmconnect.payload.request.AddressRequest;
import com.farmconnect.payload.response.AddressResponse;
import com.farmconnect.security.services.UserDetailsImpl;
import com.farmconnect.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse address = addressService.addAddress(userDetails.getId(), request);
        return ResponseEntity.ok(address);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getUserAddresses(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<AddressResponse> addresses = addressService.getUserAddresses(userDetails.getId());
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id, @Valid @RequestBody AddressRequest request) {
        AddressResponse address = addressService.updateAddress(userDetails.getId(), id, request);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id) {
        addressService.deleteAddress(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/set-default")
    public ResponseEntity<AddressResponse> setDefaultAddress(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id) {
        AddressResponse address = addressService.setDefaultAddress(userDetails.getId(), id);
        return ResponseEntity.ok(address);
    }
}
