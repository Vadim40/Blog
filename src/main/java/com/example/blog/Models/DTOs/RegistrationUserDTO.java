package com.example.blog.Models.DTOs;

import com.example.blog.Validators.Annotations.ConfirmPassword;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfirmPassword
public class RegistrationUserDTO {
    private UserDTO userDTO = new UserDTO();
    @Size(min = 8, max = 255, message = "Password must be at least 8  characters")
    private String password;

    private String ConfirmPassword;
}
