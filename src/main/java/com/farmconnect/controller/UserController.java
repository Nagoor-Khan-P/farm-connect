package com.farmconnect.controller;

import com.farmconnect.payload.request.ProfileUpdateRequest;
import com.farmconnect.payload.response.UserResponse;
import com.farmconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('BUYER') or hasRole('FARMER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @PutMapping(value = "/profile", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('BUYER') or hasRole('FARMER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @ModelAttribute ProfileUpdateRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.updateProfile(username, request, image));
    }
}
