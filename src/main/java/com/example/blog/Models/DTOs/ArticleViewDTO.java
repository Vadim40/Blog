package com.example.blog.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleViewDTO {
   private ArticleDTO articleDTO=new ArticleDTO();
   private UserDTO userDTO=new UserDTO();
   private boolean isFavorite;
   private boolean isLiked;
}
