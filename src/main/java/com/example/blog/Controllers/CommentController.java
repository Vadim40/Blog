package com.example.blog.Controllers;

import com.example.blog.Mappers.CommentMapper;
import com.example.blog.Mappers.UserMapper;
import com.example.blog.Models.Comment;
import com.example.blog.Models.DTOs.*;
import com.example.blog.Services.Implementations.CommentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentServiceImpl commentService;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<Page<CommentViewDTO>> findCommentsByArticle(@PathVariable long articleId,
                                                                      @RequestParam(defaultValue = "20") int pageSize,
                                                                      @RequestParam(defaultValue = "1") int pageNumber) {

        Pageable pageable = createPageable(pageNumber, pageSize);
        Page<Comment> comments = commentService.findParentCommentsByArticleIdOrderingByLikes(articleId, pageable);
        if (comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<CommentViewDTO> commentViewDTOS = comments.map(this::mapCommentToCommentViewDTO);
        return new ResponseEntity<>(commentViewDTOS, HttpStatus.OK);
    }

    @GetMapping("/parent-comment/{parentCommentId}")
    public ResponseEntity<Page<CommentViewDTO>> findCommentsByParentComment(@PathVariable long parentCommentId,
                                                                            @RequestParam(defaultValue = "20") int pageSize,
                                                                            @RequestParam(defaultValue = "1") int pageNumber) {
        Pageable pageable = createPageable(pageNumber, pageSize);
        Page<Comment> comments = commentService.findCommentsByParentCommentIdOrderingByLikes(parentCommentId, pageable);
        if (comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<CommentViewDTO> commentViewDTOS = comments.map(this::mapCommentToCommentViewDTO);
        return new ResponseEntity<>(commentViewDTOS, HttpStatus.OK);
    }

    @PostMapping("/{articleId}/add-to-article")
    public ResponseEntity<Object> addCommentToArticle(@PathVariable long articleId,
                                                      @RequestBody @Valid CommentDTO commentDTO,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        Comment comment = commentMapper.mapToEntity(commentDTO);
        commentService.addCommentToArticle(comment, articleId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{commentId}/add-to-comment")
    public ResponseEntity<Object> addCommentToParentComment(@PathVariable long commentId,
                                                            @RequestBody @Valid CommentDTO commentDTO,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        Comment comment = commentMapper.mapToEntity(commentDTO);
        commentService.addCommentToParentComment(comment, commentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}/toggle-like")
    public ResponseEntity<Void> toggleLike(@PathVariable long commentId) {
        commentService.toggleLikeStatus(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{commentId}/update")
    public ResponseEntity<Object> updateComment(@PathVariable long commentId,
                                                @RequestBody @Valid CommentDTO commentDTO,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        Comment comment = commentMapper.mapToEntity(commentDTO);
        commentService.updateCommentById(comment, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId) {
        commentService.deleteCommentById(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private CommentViewDTO mapCommentToCommentViewDTO(Comment comment) {
        CommentDTO commentDTO = commentMapper.mapToDTO(comment);
        UserDTO userDTO = userMapper.mapToDTO(comment.getUser());
        CommentViewDTO commentViewDTO = new CommentViewDTO();
        commentViewDTO.setCommentDTO(commentDTO);
        commentViewDTO.setUserDTO(userDTO);
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            commentViewDTO.setLiked(commentService.isCommentLiked(comment.getId()));
        }
        return commentViewDTO;
    }

    private Pageable createPageable(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber - 1, pageSize);
    }

}
