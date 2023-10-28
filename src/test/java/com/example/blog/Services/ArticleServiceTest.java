package com.example.blog.Services;

import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Image;
import com.example.blog.Models.Topic;
import com.example.blog.Models.User;
import com.example.blog.Repositories.ArticleRepository;
import com.example.blog.Repositories.UserRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
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
    public void findArticlesByUserTopicInterest(){
        Topic topicJava=Topic.builder()
                .id(1L)
                .name("java")
                .build();
        Topic topicLife=Topic.builder()
                .id(2L)
                .name("life")
                .build();
        Set<Topic> topicOfInterest=new HashSet<>();
        topicOfInterest.add(topicLife);
        topicOfInterest.add(topicJava);
        User user=User.builder()
                .topicsOfInterest(topicOfInterest)
                .build();
        Article article1=Article.builder()
                .topics(Set.of(topicJava))
                .build();
        Article article2=Article.builder()
                .topics(Set.of(topicLife,topicJava))
                .build();
        Pageable pageable= PageRequest.of(0,10);
        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);
        when(articleRepository.findArticlesByTopicsId(topicJava.getId(),pageable))
                .thenReturn(new PageImpl<>(List.of(article1,article2),pageable,2));
        when(articleRepository.findArticlesByTopicsId(topicLife.getId(),pageable))
                .thenReturn(new PageImpl<>(List.of(article2),pageable,1));
        Page<Article> articlesUserTopicsOfInterest=articleService.findArticlesByUserTopicOfInterest(10,0);

        Assertions.assertThat(articlesUserTopicsOfInterest.getTotalElements()).isEqualTo(2);

    }




    @Test
    public void updateArticleById_UserRole_Test() {
        User user=new User();
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
        User user=new User();
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
        User user=new User();
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
        when(userRepository.save(any(User.class))).thenReturn(user);
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
