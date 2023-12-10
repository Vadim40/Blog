package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findParentCommentsByArticleIdOrderingByLikes(long articleId);

    List<Comment> findCommentsByParentCommentIdOrderingByLikes(long parentCommentID);

    Comment findCommentById(long commentId);



    Comment addCommentToArticle(Comment comment, long articleId);
    Comment addCommentToParentComment(Comment comment, long parentCommentId);
    Comment updateCommentById(Comment comment, long commentId);

    void deleteCommentById(long id);

    void toggleLikeStatus(long commentId);

    boolean isCommentLiked(long commentId);

}
