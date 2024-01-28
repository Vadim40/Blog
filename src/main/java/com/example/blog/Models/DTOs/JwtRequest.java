package com.example.blog.Models.DTOs;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
