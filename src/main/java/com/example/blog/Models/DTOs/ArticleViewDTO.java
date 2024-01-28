package com.example.blog.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleViewDTO {
    private ArticleDTO articleDTO = new ArticleDTO();
    private UserProfileDTO userProfileDTO = new UserProfileDTO();
    private boolean isFavorite;
    private boolean isLiked;
}
