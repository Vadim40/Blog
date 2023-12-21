package com.example.blog.Mappers;

import com.example.blog.Models.Comment;
import com.example.blog.Models.DTOs.CommentDTO;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentDTO mapToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setCreationDate(comment.getCreationDate());
        commentDTO.setText(comment.getText());
        commentDTO.setLikes(comment.getLikes());
        return commentDTO;
    }

    public Comment mapToEntity(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setCreationDate(commentDTO.getCreationDate());
        comment.setText(commentDTO.getText());
        comment.setLikes(commentDTO.getLikes());
        return comment;
    }
}
