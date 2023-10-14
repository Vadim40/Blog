package com.example.blog.Services;

import com.example.blog.Excteptions.CommentNotFoundException;
import com.example.blog.Models.Article;
import com.example.blog.Models.Comment;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.User;
import com.example.blog.Repositories.CommentRepository;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Interfaces.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final ArticleService articleService;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Set<Comment> findCommentsByArticleId(long articleId) {
        return commentRepository.findCommentsByArticleId(articleId);
    }
    @Override
    public Set<Comment> findCommentsByUserId(long userId) {
        return commentRepository.findCommentsByUserId(userId);
    }
    @Override
    public Set<Comment> findCommentsByParentCommentId(long parentCommentID) {
        return commentRepository.findCommentsByParentCommentId(parentCommentID);
    }
    @Override
    public Comment findCommentById(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("Comment not Found"));
    }

    @Override
    public Comment addCommentToArticle(Comment comment, long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Article article = articleService.findArticleById(articleId);
        comment.setUser(authenticatedUser);
        comment.setArticle(article);
        comment.setCreationDate(LocalDate.now());
        return commentRepository.save(comment);
    }

    @Override
    public Comment addCommentToParentComment(Comment comment, long parentCommentId) {
        User authenticatedUser=customUserDetailsService.getAuthenticatedUser();
        Comment parentComment=findCommentById(parentCommentId);
        if(parentComment.getParentComment()!=null){
            throw new IllegalArgumentException("Nested comments are not allowed.");
        }
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
    public void putLike(long commentId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Comment comment = findCommentById(commentId);
        if (authenticatedUser.getLikedComments().contains(commentId)) {
            throw new IllegalArgumentException("You have already  liked this comment");
        }
        authenticatedUser.getLikedComments().add(commentId);
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
        userRepository.save(authenticatedUser);
    }
    @Override
    @Transactional
    public void removeLike(long commentId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Comment comment = findCommentById(commentId);
        if (!authenticatedUser.getLikedComments().contains(commentId)) {
            throw new IllegalArgumentException("You have not  liked this comment");
        }
        authenticatedUser.getLikedComments().remove(commentId);
        comment.setLikes(comment.getLikes() - 1);
        commentRepository.save(comment);
        userRepository.save(authenticatedUser);
    }
}
