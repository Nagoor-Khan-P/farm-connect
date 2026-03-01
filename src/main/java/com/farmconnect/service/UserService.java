package com.farmconnect.service;

import com.farmconnect.payload.request.ProfileUpdateRequest;
import com.farmconnect.payload.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse getUserProfile(String username);

    UserResponse updateProfile(String username, ProfileUpdateRequest request, MultipartFile image);
}
