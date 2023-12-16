package com.example.blog.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleViewDTO {
   private ArticleDTO articleDTO;
   private UserDTO userDTO;
   private boolean isFavorite=false;
   private boolean isLiked=false;
}
