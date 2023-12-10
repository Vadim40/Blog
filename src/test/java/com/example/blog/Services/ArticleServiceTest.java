package com.example.blog.Services;

import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Image;
import com.example.blog.Models.Topic;
import com.example.blog.Models.User;
import com.example.blog.Repositories.ArticleRepository;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Impementations.ArticleServiceImpl;
import com.example.blog.Services.Impementations.CustomUserDetailsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ArticleRepository articleRepository;


    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private ArticleServiceImpl articleService;


    @Test
    public void findFavoriteArticles() {
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
                .favoriteArticles(articles)
                .build();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

        Set<Article> savedArticles = articleService.findFavoriteArticlesByAuthenticationUser();

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
    public void publishArticle_ThrowException() {
        Article article = Article.builder()
                .published(true)
                .build();
        when(articleRepository.findById(any(Long.class))).thenReturn(Optional.of(article));
        Assertions.assertThatThrownBy(() -> articleService.publishArticle(anyLong())).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    public void findArticlesByUserTopicInterest() {
        Topic topicJava = Topic.builder()
                .id(1L)
                .name("java")
                .build();
        Topic topicLife = Topic.builder()
                .id(2L)
                .name("life")
                .build();
        Set<Topic> topicOfInterest = new HashSet<>();
        topicOfInterest.add(topicLife);
        topicOfInterest.add(topicJava);
        User user = User.builder()
                .topicsOfInterest(topicOfInterest)
                .build();
        Article article1 = Article.builder()
                .topics(Set.of(topicJava))
                .build();
        Article article2 = Article.builder()
                .topics(Set.of(topicLife, topicJava))
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.findArticlesByTopicsName(topicJava.getName(), pageable))
                .thenReturn(new PageImpl<>(List.of(article1, article2), pageable, 2));
        when(articleRepository.findArticlesByTopicsName(topicLife.getName(), pageable))
                .thenReturn(new PageImpl<>(List.of(article2), pageable, 1));
        Page<Article> articlesUserTopicsOfInterest = articleService.findArticlesByUserTopicsOfInterest(10, 0);

        Assertions.assertThat(articlesUserTopicsOfInterest.getTotalElements()).isEqualTo(2);

    }


    @Test
    public void updateArticleById_UserRole_Test() {
        User user = new User();
        user.getRoles().add(Role.USER);
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
        User user = new User();
        user.getRoles().add(Role.ADMIN);
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
        User user = new User();
        user.getRoles().add(Role.USER);
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
    public void toggleLike_Test() {
        User user = User.builder()
                .username("valentine")
                .likedArticles(new HashSet<>())
                .build();
        long articleId1 = 1L;
        Article article1 = Article.builder()
                .id(articleId1)
                .likes(9)
                .text("some")
                .build();
        long articleId2 = 2L;
        Article article2 = Article.builder()
                .id(articleId1)
                .likes(9)
                .text("some")
                .build();
        user.getLikedArticles().add(articleId2);
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.findById(articleId1)).thenReturn(Optional.of(article1));
        when(articleRepository.save(article1)).thenReturn(article1);
        when(articleRepository.findById(articleId2)).thenReturn(Optional.of(article2));
        when(articleRepository.save(article2)).thenReturn(article2);
        when(userRepository.save(any(User.class))).thenReturn(user);
        articleService.toggleLikeStatus(articleId1);
        articleService.toggleLikeStatus(articleId2);

        Assertions.assertThat(article1.getLikes()).isEqualTo(10);
        Assertions.assertThat(article2.getLikes()).isEqualTo(8);
    }


    @Test
    void saveImageToArticle() {
        Article article = Article.builder()
                .text("some")
                .images(new ArrayList<>())
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
