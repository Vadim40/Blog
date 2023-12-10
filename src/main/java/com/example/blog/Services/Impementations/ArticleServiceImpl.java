package com.example.blog.Services.Impementations;

import com.example.blog.Excteptions.ArticleNotFoundException;
import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Image;
import com.example.blog.Models.Topic;
import com.example.blog.Models.User;
import com.example.blog.Repositories.ArticleRepository;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Interfaces.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;


    @Override
    public Page<Article> findArticlesByUser_Username(String username, int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return articleRepository.findArticlesByUser_Username(username, pageable);
    }

    @Override
    public Set<Article> findFavoriteArticlesByAuthenticationUser() {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        return authenticationUser.getFavoriteArticles();
    }


    @Override
    public Page<Article> findArticlesByTopicName(String topicName, int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return articleRepository.findArticlesByTopicsName(topicName, pageable);
    }

    @Override
    public Page<Article> findArticlesByUserTopicsOfInterest(int pageSize, int pageNumber) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Set<Article> articles = new HashSet<>();
        for (Topic topic : authenticatedUser.getTopicsOfInterest()) {
            articles.addAll(findArticlesByTopicName(topic.getName(), pageSize, pageNumber).getContent());
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new PageImpl<>(List.copyOf(articles), pageable, articles.size());
    }


    @Override
    public Page<Article> findArticlesByTitleIsContainingIgnoreCaseString(String title, int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return articleRepository.findArticlesByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    public Page<Article> findArticlesBySorting(int pageSize, int pageNumber, Sort.Direction direction, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, direction, sortBy);
        return articleRepository.findAll(pageable);
    }

    @Override
    public Article findArticleById(long articleId) {
        return articleRepository.findById(articleId).orElseThrow(() -> new ArticleNotFoundException("Article not found."));
    }

    @Override
    public Article publishArticle(long articleId) {
        Article article = findArticleById(articleId);
        if (article.isPublished()) {
            throw new IllegalArgumentException("You are already saved0 this user.");
        }
        article.setPublished(true);
        return articleRepository.save(article);
    }

    @Override
    @Transactional
    public Article toggleFavoriteStatus(long articleId) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        Article article = findArticleById(articleId);
        if (authenticationUser.getFavoriteArticles().contains(article)) {
            authenticationUser.getFavoriteArticles().remove(article);
        } else {
            authenticationUser.getFavoriteArticles().add(article);
        }
        userRepository.save(authenticationUser);
        return articleRepository.save(article);
    }


    @Override
    @Transactional
    public Article saveArticle(Article article) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        authenticationUser.getArticles().add(article);
        article.setUser(authenticationUser);
        article.setCreationDate(LocalDate.now());
        return articleRepository.save(article);
    }

    @Override
    @Transactional
    public Article saveImageToArticle(Image image, long articleId) {
        Article article = findArticleById(articleId);
        String text = article.getText().concat(" [image:" + article.getImages().size() + 1 + "] ");
        article.setText(text);
        image.setArticle(article);
        article.getImages().add(image);
        return articleRepository.save(article);
    }

    @Override
    public Article updateArticleById(Article article, long articleId) {
        checkArticleAccess(articleId);
        if (findArticleById(articleId).isPublished()) {
            throw new UnsupportedOperationException("You dont have permission to update this article");
        }
        article.setId(articleId);
        return articleRepository.save(article);
    }

    @Override
    public void deleteArticleById(long articleId) {
        checkArticleAccess(articleId);
        articleRepository.deleteById(articleId);
    }

    private void checkArticleAccess(long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Article articleToCheck = findArticleById(articleId);
        if (!authenticatedUser.getRoles().contains(Role.ADMIN) && !authenticatedUser.getArticles().contains(articleToCheck)) {
            throw new AccessDeniedException("You don't have permission to perform this action on this article");
        }
    }

    @Override
    @Transactional
    public Article toggleLikeStatus(long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Article article = findArticleById(articleId);
        if (authenticatedUser.getLikedArticles().contains(articleId)) {
            article.setLikes(article.getLikes() - 1);
            authenticatedUser.getLikedArticles().remove(articleId);
        } else {
            article.setLikes(article.getLikes() + 1);
            authenticatedUser.getLikedArticles().add(articleId);
        }

        userRepository.save(authenticatedUser);
        return articleRepository.save(article);
    }

    @Override
    public boolean isArticleFavorite(long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Article article = findArticleById(articleId);
        return authenticatedUser.getFavoriteArticles().contains(article);
    }

    @Override
    public boolean isArticleLiked(long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        return authenticatedUser.getLikedArticles().contains(articleId);
    }

}
