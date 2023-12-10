package com.example.blog.Services;

import com.example.blog.Models.Article;
import com.example.blog.Models.Comment;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.User;
import com.example.blog.Repositories.CommentRepository;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Impementations.ArticleServiceImpl;
import com.example.blog.Services.Impementations.CommentServiceImpl;
import com.example.blog.Services.Impementations.CustomUserDetailsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleServiceImpl articleService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void addCommentToArticle() {
        User user = User.builder()
                .username("crossfade")
                .build();
        Article article = new Article();
        Comment comment = Comment.builder()
                .text("comment")
                .build();

        when(commentRepository.save(comment)).thenReturn(comment);
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleService.findArticleById(anyLong())).thenReturn(article);

        commentService.addCommentToArticle(comment, 1L);
        System.out.println(article.getComments());

        Assertions.assertThat(comment.getUser()).isEqualTo(user);
        Assertions.assertThat(comment.getArticle()).isEqualTo(article);
    }

    @Test
    public void toggleLike_Test() {
        User user = new User();
        Comment comment = Comment.builder()
                .text("comment")
                .likes(9)
                .build();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepository.save(any(User.class))).thenReturn(user);
        commentService.toggleLikeStatus(1L);

        Assertions.assertThat(comment.getLikes()).isEqualTo(10);
    }

    @Test
    public void addCommentToParentComment_ThrowException_Test() {
        User user = User.builder()
                .username("crossfade")
                .build();
        Article article = new Article();
        Comment parentComment = Comment.builder()
                .text("parent-comment")
                .build();
        Comment comment = Comment.builder()
                .text("comment")
                .parentComment(parentComment)
                .article(article)
                .build();
        Comment newComment = Comment.builder()
                .text("asdf")
                .build();


        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(comment));


        Assertions.assertThatThrownBy(() -> commentService.addCommentToParentComment(newComment, 1L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateCommentById_ByUserRole_Test() {
        User user = new User();
        user.getRoles().add(Role.USER);
        long commentId = 1L;
        Comment commentToUpdate = Comment.builder()
                .id(commentId)
                .text("comment")
                .build();
        Comment comment = new Comment();
        user.getComments().add(commentToUpdate);
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(commentToUpdate));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment commentToCheck = commentService.updateCommentById(comment, commentId);

        Assertions.assertThat(commentToCheck.getId()).isEqualTo(commentId);
    }

    @Test
    public void updateCommentById_ByAdminRole_Test() {
        User user = new User();
        user.getRoles().add(Role.ADMIN);
        long commentId = 1L;
        Comment commentToUpdate = Comment.builder()
                .id(commentId)
                .text("comment")
                .build();
        Comment comment = new Comment();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(commentToUpdate));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment commentToCheck = commentService.updateCommentById(comment, commentId);

        Assertions.assertThat(commentToCheck.getId()).isEqualTo(commentId);
    }

    @Test
    public void updateCommentById_ThrowException_Test() {
        User user = new User();
        user.getRoles().add(Role.USER);
        long commentId = 1L;
        Comment commentToUpdate = Comment.builder()
                .id(commentId)
                .text("comment")
                .build();
        Comment comment = new Comment();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(commentToUpdate));
        Assertions.assertThatThrownBy(() -> commentService.updateCommentById(comment, commentId)).isInstanceOf(AccessDeniedException.class);


    }

}
