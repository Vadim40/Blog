package com.example.blog.Repositories;

import com.example.blog.Models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByParentCommentIsNullAndArticleId(long articleId);
    List<Comment> findCommentsByParentCommentId(long parentCommentId);

 }