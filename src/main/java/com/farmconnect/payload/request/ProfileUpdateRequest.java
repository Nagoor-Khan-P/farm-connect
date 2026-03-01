package com.farmconnect.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(max = 50)
    private String email;

    // Password change could be added here separately if needed,
    // but the user only asked for firstname, lastname, email, and image.
}
