package com.example.blog.Services;

import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Image;
import com.example.blog.Models.User;
import com.example.blog.Repositories.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private ArticleService articleService;


    @Test
    public void findSavedArticles() {
        Article article1 = Article.builder()
                .text("some")
                .build();
        Article article2 = Article.builder()
                .text("some")
                .build();
        Set<Article> articles = new HashSet<>();
        articles.add(article1);
        articles.add(article2);
        User user = User.builder()
                .username("colors")
                .savedArticles(articles)
                .build();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

        Set<Article> savedArticles = articleService.findSavedArticlesByAuthenticationUser();

        Assertions.assertThat(savedArticles).isEqualTo(articles);
    }


    @Test
    public void saveArticle() {
        User user = User.builder()
                .username("valentine")
                .build();
        Article article = Article.builder()
                .text("some")
                .build();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        articleService.saveArticle(article);

        Assertions.assertThat(article.getUser()).isEqualTo(user);
    }

    @Test
    public void updateArticleById_UserRole_Test() {
        User user = User.builder()
                .role(Role.USER)
                .username("valentine")
                .articles(new HashSet<>())
                .build();
        long articleId = 1L;
        Article articleToUpdate = Article.builder()
                .id(articleId)
                .text("some")
                .build();

        Article article = new Article();
        user.getArticles().add(articleToUpdate);
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        when(articleRepository.findById(any(Long.class))).thenReturn(Optional.of(articleToUpdate));

        articleService.updateArticleById(article, articleId);
        Assertions.assertThat(article.getId()).isEqualTo(articleId);

    }

    @Test
    public void updateArticleById_AdminRole_Test() {
        User user = User.builder()
                .role(Role.ADMIN)
                .username("valentine")
                .articles(new HashSet<>())
                .build();
        long articleId = 1L;
        Article articleToUpdate = Article.builder()
                .id(articleId)
                .text("some")
                .build();

        Article article = new Article();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        when(articleRepository.findById(any(Long.class))).thenReturn(Optional.of(articleToUpdate));

        articleService.updateArticleById(article, articleId);
        Assertions.assertThat(article.getId()).isEqualTo(articleId);

    }

    @Test
    public void updateArticleById_ThrowException_Test() {
        User user = User.builder()
                .role(Role.USER)
                .username("valentine")
                .articles(new HashSet<>())
                .build();
        long articleId = 1L;
        Article articleToUpdate = Article.builder()
                .id(articleId)
                .text("some")
                .build();

        Article article = new Article();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.findById(any(Long.class))).thenReturn(Optional.of(articleToUpdate));

        Assertions.assertThatThrownBy(() -> articleService.updateArticleById(article, articleId)).isInstanceOf(AccessDeniedException.class);


    }

    @Test
    public void putLike_Test() {
        User user = User.builder()
                .username("valentine")
                .likedArticles(new HashSet<>())
                .build();
        long articleId = 1L;
        Article article = Article.builder()
                .id(articleId)
                .likes(9)
                .text("some")
                .build();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.findById(any(Long.class))).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        when(userService.saveUser(any(User.class))).thenReturn(user);
        articleService.putLike(articleId);

        Assertions.assertThat(article.getLikes()).isEqualTo(10);
    }

    @Test
    public void putLike_ThrowException_Test() {
        User user = User.builder()
                .username("valentine")
                .likedArticles(new HashSet<>())
                .build();
        long articleId = 1L;
        Article article = Article.builder()
                .id(articleId)
                .likes(9)
                .text("some")
                .build();
        user.getLikedArticles().add(articleId);
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.findById(any(Long.class))).thenReturn(Optional.of(article));

        Assertions.assertThatThrownBy(() -> articleService.putLike(articleId)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void saveImageToArticle() {
        Article article = Article.builder()
                .text("some")
                .images(new HashSet<>())
                .build();
        Image image = Image.builder()
                .imageData(new byte[]{1, 2, 3, 5})
                .build();

        when(articleRepository.findById(any(Long.class))).thenReturn(Optional.of(article));
        when(articleRepository.save(article)).thenReturn(article);

        articleService.saveImageToArticle(image, 1L);

        Assertions.assertThat(article.getImages().size()).isEqualTo(1);
    }


}
