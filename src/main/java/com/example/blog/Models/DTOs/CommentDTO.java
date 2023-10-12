package com.example.blog.Models.DTOs;


import jakarta.validation.constraints.NotBlank;
import lombok.*;


import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentDTO {
    private long id;
    @NotBlank
    private String text;
    private int likes;
    private LocalDate creationDate;

}