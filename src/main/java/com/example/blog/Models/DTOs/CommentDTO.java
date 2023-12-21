package com.example.blog.Models.DTOs;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentDTO {
    private long id;
    @NotBlank
    @Size(max = 800,message = "no more than 800 characters")
    private String text;
    private int likes;
    private LocalDate creationDate;

}