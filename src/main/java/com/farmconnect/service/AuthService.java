package com.farmconnect.service;

import com.farmconnect.payload.request.LoginRequest;
import com.farmconnect.payload.request.SignupRequest;
import com.farmconnect.payload.response.JwtResponse;
import com.farmconnect.payload.response.MessageResponse;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);

    MessageResponse registerUser(SignupRequest signupRequest);
}
