package com.example.blog.Mappers;

import com.example.blog.Models.Comment;
import com.example.blog.Models.DTOs.CommentDTO;
import com.example.blog.Models.DTOs.CommentViewDTO;
import com.example.blog.Models.DTOs.UserDTO;
import org.springframework.stereotype.Component;

@Component

public class CommentMapper {
    private final UserMapper userMapper = new UserMapper();

    public CommentDTO mapToCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setCreationDate(comment.getCreationDate());
        commentDTO.setText(comment.getText());
        commentDTO.setLikes(comment.getLikes());
        return commentDTO;
    }

    public Comment mapToComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setCreationDate(commentDTO.getCreationDate());
        comment.setText(commentDTO.getText());
        comment.setLikes(commentDTO.getLikes());
        return comment;
    }

    public CommentViewDTO mapToCommentViewDTO(Comment comment, boolean isCommentLiked) {
        UserDTO userDTO = userMapper.mapToUserDTO(comment.getUser());
        CommentViewDTO commentViewDTO = new CommentViewDTO();
        commentViewDTO.setCommentDTO(mapToCommentDTO(comment));
        commentViewDTO.getUserProfileDTO().setAvatar(userDTO.getAvatar());
        commentViewDTO.getUserProfileDTO().setUsername(userDTO.getUsername());
        commentViewDTO.setLiked(isCommentLiked);
        return commentViewDTO;
    }
}
