package com.example.blog.Models.DTOs;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ArticleDTO {
    private long id;
    @NotBlank
    @Size(max = 20000, message = "no more than 200000 characters")
    private String text;
    private String title;
    private int likes;
    private LocalDate creationDate;
    private List<ImageDTO> images;
    @Size(max = 3, message = "Maximum of 3 topics allowed")
    private List<TopicDTO> topics;

}