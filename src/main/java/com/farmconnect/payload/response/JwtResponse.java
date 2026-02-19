package com.farmconnect.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, UUID id, String username, String firstName, String lastName, String email,
            List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }
}
