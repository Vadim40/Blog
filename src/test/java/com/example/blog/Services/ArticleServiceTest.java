package com.example.blog.Services;

import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Image;
import com.example.blog.Models.Topic;
import com.example.blog.Models.User;
import com.example.blog.Repositories.ArticleRepository;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Implementations.ArticleServiceImpl;
import com.example.blog.Services.Implementations.CustomUserDetailsService;
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
                .text("some2")
                .build();
        List<Article> articles = new ArrayList<>();
        articles.add(article1);
        articles.add(article2);
        User user = User.builder()
                .username("colors")
                .favoriteArticles(articles)
                .build();
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Article> savedArticles = articleService.findPublishedFavoriteArticlesByAuthenticationUser(pageable);

        Assertions.assertThat(savedArticles.getTotalElements()).isEqualTo(2);
    }


    @Test
    public void saveArticle() {
        User user = User.builder()
                .articles(new ArrayList<>())
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
        List<Topic> topicOfInterest = new ArrayList<>();
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
        when(articleRepository.findArticlesByPublishedIsTrueAndTopicsName(topicJava.getName(), pageable))
                .thenReturn(new PageImpl<>(List.of(article1, article2), pageable, 2));
        when(articleRepository.findArticlesByPublishedIsTrueAndTopicsName(topicLife.getName(), pageable))
                .thenReturn(new PageImpl<>(List.of(article2), pageable, 1));
        Page<Article> articlesUserTopicsOfInterest = articleService.findPublishedArticlesByUserTopicsOfInterest(pageable);

        Assertions.assertThat(articlesUserTopicsOfInterest.getTotalElements()).isEqualTo(2);

    }


    @Test
    public void updateArticleById_UserRole_Test() {
        User user = new User();
        user.getRoles().add(Role.ROLE_USER);
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
        user.getRoles().add(Role.ROLE_ADMIN);
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
        user.getRoles().add(Role.ROLE_USER);
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
                .likedArticles(new ArrayList<>())
                .build();
        long articleId1 = 1L;
        Article article1 = Article.builder()
                .id(articleId1)
                .likes(9)
                .text("some")
                .build();
        long articleId2 = 2L;
        Article article2 = Article.builder()
                .id(articleId2)
                .likes(9)
                .text("some")
                .build();
        user.getLikedArticles().add(articleId2);
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.findArticleByIdAndPublishedIsTrue(articleId1)).thenReturn(Optional.of(article1));
        when(articleRepository.save(article1)).thenReturn(article1);
        when(articleRepository.findArticleByIdAndPublishedIsTrue(articleId2)).thenReturn(Optional.of(article2));
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
