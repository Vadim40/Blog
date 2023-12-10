package com.example.blog.Controllers;

import com.example.blog.Mappers.CommentMapper;
import com.example.blog.Models.Comment;
import com.example.blog.Models.DTOs.CommentDTO;
import com.example.blog.Services.Impementations.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentServiceImpl commentService;
    private final CommentMapper commentMapper;

    @GetMapping("/article/{articleId}")
    public String findCommentsByArticle(
            @PathVariable long articleId,
            Model model) {
        List<Comment> comments = commentService.findParentCommentsByArticleIdOrderingByLikes(articleId);
        List<CommentDTO> commentDTOS = comments.stream().map(commentMapper::mapToDTO).collect(Collectors.toList());
        model.addAttribute("comments", commentDTOS);
        return "comments-article";
    }


    @GetMapping("/parent-comment/{parentCommentId}")
    public String findCommentsByParentComment(
            @PathVariable long parentCommentId,
            Model model) {
        List<Comment> comments = commentService.findCommentsByParentCommentIdOrderingByLikes(parentCommentId);
        List<CommentDTO> commentDTOS = comments.stream().map(commentMapper::mapToDTO).collect(Collectors.toList());
        model.addAttribute("comments", commentDTOS);
        return "comments-parent";
    }

    @PostMapping("/{articleId}/add-to-aricle")
    public String addCommentToArticle(
            @PathVariable long articleId,
            @RequestBody CommentDTO commentDTO) {
        Comment comment = commentMapper.mapToEntity(commentDTO);
        commentService.addCommentToArticle(comment, articleId);
        return "some";
    }

    @PostMapping("/{commentId}/add-to-comment")
    public String addCommentToParentComment(
            @PathVariable long commentId,
            @RequestBody CommentDTO commentDTO) {
        Comment comment = commentMapper.mapToEntity(commentDTO);
        commentService.addCommentToParentComment(comment, commentId);
        return "some";
    }

    @PutMapping("/{commentId}/toggle-like")
    public String toggleLike(@PathVariable long commentId) {
        commentService.toggleLikeStatus(commentId);
        return "some";
    }

    @PutMapping("/{commentId}/update")
    public String updateComment(
            @PathVariable long commentId,
            @ModelAttribute CommentDTO commentDTO) {
        Comment comment = commentMapper.mapToEntity(commentDTO);
        commentService.updateCommentById(comment, commentId);
        return "some";
    }

    @DeleteMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable long commentId) {
        commentService.deleteCommentById(commentId);
        return "some";
    }


}
