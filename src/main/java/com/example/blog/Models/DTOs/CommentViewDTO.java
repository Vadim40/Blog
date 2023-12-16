package com.example.blog.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentViewDTO {
    private CommentDTO commentDTO;
    private UserDTO userDTO;
    private boolean isLiked=false;
}
