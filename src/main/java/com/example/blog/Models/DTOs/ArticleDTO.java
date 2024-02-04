package com.example.blog.Models.DTOs;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @Valid
    private List<ImageDTO> images;
    @Size(max = 3, message = "Maximum of 3 topics allowed")
    private List<TopicDTO> topics=new ArrayList<>();

}