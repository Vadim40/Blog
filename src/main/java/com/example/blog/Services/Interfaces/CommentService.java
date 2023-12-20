package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    Page<Comment> findParentCommentsByArticleIdOrderingByLikes(long articleId, Pageable pageable);

    Page<Comment> findCommentsByParentCommentIdOrderingByLikes(long parentCommentId, Pageable pageable);

    Comment findCommentById(long commentId);

    Comment addCommentToArticle(Comment comment, long articleId);

    Comment addCommentToParentComment(Comment comment, long parentCommentId);

    Comment updateCommentById(Comment comment, long commentId);

    void deleteCommentById(long id);

    void toggleLikeStatus(long commentId);

    boolean isCommentLiked(long commentId);

}
