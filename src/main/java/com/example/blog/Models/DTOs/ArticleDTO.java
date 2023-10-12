package com.example.blog.Models.DTOs;


import com.example.blog.Models.Enums.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ArticleDTO {
    private long id;
    @NotBlank
    private String text;
    private String title;
    private int likes;
    private LocalDate creationDate;
    @NotBlank
    private Category category;
    private Set<String> tags;


}