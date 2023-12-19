package com.example.blog.Services.Implementations;

import com.example.blog.Excteptions.CommentNotFoundException;
import com.example.blog.Models.Article;
import com.example.blog.Models.Comment;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.User;
import com.example.blog.Repositories.CommentRepository;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Interfaces.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ArticleServiceImpl articleService;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public List<Comment> findParentCommentsByArticleIdOrderingByLikes(long articleId) {
        List<Comment> comments = commentRepository.findCommentsByParentCommentIsNullAndArticleId(articleId);
        return sortCommentsByLikes(comments);
    }

    private List<Comment> sortCommentsByLikes(List<Comment> comments) {
        return comments.stream()
                .sorted(Comparator.comparingInt(Comment::getLikes).reversed())
                .collect(Collectors.toList());
    }


    @Override
    public List<Comment> findCommentsByParentCommentIdOrderingByLikes(long parentCommentID) {
        List<Comment> comments = commentRepository.findCommentsByParentCommentId(parentCommentID);
        return sortCommentsByLikes(comments);
    }

    @Override
    public Comment findCommentById(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("Comment not Found"));
    }

    @Override
    @Transactional
    public Comment addCommentToArticle(Comment comment, long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Article article = articleService.findPublishedArticleById(articleId);
        authenticatedUser.getComments().add(comment);
        article.getComments().add(comment);
        comment.setUser(authenticatedUser);
        comment.setArticle(article);
        comment.setCreationDate(LocalDate.now());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment addCommentToParentComment(Comment comment, long parentCommentId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Comment parentComment = findCommentById(parentCommentId);
        Article article = articleService.findPublishedArticleById(parentComment.getArticle().getId());
        if (parentComment.getParentComment() != null) {
            throw new IllegalArgumentException("Nested comments are not allowed.");
        }
        authenticatedUser.getComments().add(comment);
        article.getComments().add(comment);
        comment.setUser(authenticatedUser);
        comment.setArticle(parentComment.getArticle());
        comment.setParentComment(parentComment);
        comment.setCreationDate(LocalDate.now());
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateCommentById(Comment comment, long commentId) {
        checkCommentAccess(commentId);
        comment.setId(commentId);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteCommentById(long commentId) {
        checkCommentAccess(commentId);
        commentRepository.deleteById(commentId);
    }

    private void checkCommentAccess(long commentId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Comment commentToCheck = findCommentById(commentId);
        if (!authenticatedUser.getRoles().contains(Role.ADMIN) && !authenticatedUser.getComments().contains(commentToCheck)) {
            throw new AccessDeniedException("You don't have permission to perform this action on the comment");
        }
    }

    @Override
    @Transactional
    public void toggleLikeStatus(long commentId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Comment comment = findCommentById(commentId);
        if (authenticatedUser.getLikedComments().contains(commentId)) {
            authenticatedUser.getLikedComments().remove(commentId);
            comment.setLikes(comment.getLikes() - 1);
        } else {
            authenticatedUser.getLikedComments().add(commentId);
            comment.setLikes(comment.getLikes() + 1);
        }
        commentRepository.save(comment);
        userRepository.save(authenticatedUser);
    }

    @Override
    public boolean isCommentLiked(long commentId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        return authenticatedUser.getLikedComments().contains(commentId);
    }
}
