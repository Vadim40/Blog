package com.example.blog.Models.DTOs;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private long id;
    @Size(max = 50,message = "no more than 50 characters")
    private String name;
    @Size(max = 1000)
    private byte[] imageData;
}
