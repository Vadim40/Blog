package com.example.blog.Models.DTOs;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ArticleDTO {
    private long id;
    @NotBlank
    @Max(value = 20000, message = "no more than 200000 characters")
    private String text;
    private String title;
    private int likes;
    private LocalDate creationDate;


}