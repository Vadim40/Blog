package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Comment;

import java.util.Set;

public interface CommentService {
    Set<Comment> findCommentsByArticleId(long articleId);

    Set<Comment> findCommentsByUserId(long userId);

    Set<Comment> findCommentsByParentCommentId(long parentCommentID);

    Comment findCommentById(long commentId);



    Comment addCommentToArticle(Comment comment, long articleId);
    Comment addCommentToParentComment(Comment comment, long parentCommentId);
    Comment updateCommentById(Comment comment, long commentId);

    void deleteCommentById(long id);

    void putLike(long commentId);

    void removeLike(long commentId);
}
