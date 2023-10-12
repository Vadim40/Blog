package com.example.blog.Repositories;

import com.example.blog.Models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Set<Comment> findCommentsByArticleId(long articleId);
    Set<Comment> findCommentsByUserId(long userId);
    Set<Comment> findCommentsByParentCommentId(long parentCommentId);
 }