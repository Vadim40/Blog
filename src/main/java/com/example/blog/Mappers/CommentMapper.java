package com.example.blog.Mappers;

import com.example.blog.Models.Comment;
import com.example.blog.Models.DTOs.CommentDTO;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class CommentMapper {
    public CommentDTO mapToDTO(Comment comment){
        return CommentDTO.builder()
                .id(comment.getId())
                .creationDate(comment.getCreationDate())
                .text(comment.getText())
                .likes(comment.getLikes())
                .build();
    }
    public Comment mapToEntity(CommentDTO commentDTO){
        return Comment.builder()
                .id(commentDTO.getId())
                .creationDate(commentDTO.getCreationDate())
                .text(commentDTO.getText())
                .likes(commentDTO.getLikes())
                .build();
    }
}
