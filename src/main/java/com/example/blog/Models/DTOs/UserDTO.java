package com.example.blog.Models.DTOs;

import com.example.blog.Validators.Annotations.UniqueUsername;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
    @NotBlank(message = "firstname should be no empty")
    private String firstname;
    @NotBlank (message = "lastname should be no empty")
    private String lastname;
    @Email(message = "it is not email")
    private String email;
    @Size(max = 500,message = "no more than 500 symbols")
    private String selfDescription;
    private LocalDate creationDate;
    @NotBlank(message = "username should be no empty")
    @Size(max = 25, message = "username should be no more than 25 characters")
    @UniqueUsername
    private String username;
    @Valid
    private ImageDTO avatar=new ImageDTO();

}