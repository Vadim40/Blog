package com.example.blog.Models.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
    private long id;
    @NotBlank(message = "firstname should be no empty")
    private String firstname;
    @NotBlank (message = "lastname should be no empty")
    private String lastname;
    @Email(message = "it is not email")
    private String email;
    @Max(value = 500,message = "no more than 500 symbols")
    private String selfDescription;
    private LocalDate creationDate;
    @NotBlank(message = "password should be no empty")
    @Size(min = 8, max = 255, message = "Password must be at least 8  characters")
    private String password;
    @NotBlank(message = "username should be no empty")
    private String username;
    private ImageDTO avatar;

}