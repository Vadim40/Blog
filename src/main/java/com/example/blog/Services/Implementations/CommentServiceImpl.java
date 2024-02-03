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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ArticleServiceImpl articleService;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Page<Comment> findParentCommentsByArticleIdOrderingByLikes(long articleId, Pageable pageable) {
        Sort sort = Sort.by(Sort.Order.desc("likes"));
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return commentRepository.findCommentsByParentCommentIsNullAndArticleId(articleId, sortedPageable);
    }


    @Override
    public Page<Comment> findCommentsByParentCommentIdOrderingByLikes(long parentCommentID, Pageable pageable) {
        Sort sort = Sort.by(Sort.Order.desc("likes"));
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return commentRepository.findCommentsByParentCommentId(parentCommentID, sortedPageable);
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
        if (!authenticatedUser.getRoles().contains(Role.ROLE_ADMIN) && !authenticatedUser.getComments().contains(commentToCheck)) {
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
        if(! customUserDetailsService.isUserAuthenticated()){
            return false;
        }
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        return authenticatedUser.getLikedComments().contains(commentId);
    }
}
