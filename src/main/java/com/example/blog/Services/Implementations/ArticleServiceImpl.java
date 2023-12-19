package com.example.blog.Services.Implementations;

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
import java.util.ArrayList;
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
    public Page<Article> findPublishedArticlesByUserUsername(String username, Pageable pageable) {
        return articleRepository.findArticlesByPublishedIsTrueAndUserUsername(username, pageable);
    }

    @Override
    public Page<Article> findPublishedFavoriteArticlesByAuthenticationUser(Pageable pageable) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        List<Article> favoriteArticles = authenticationUser.getFavoriteArticles();
        return mapSetArticlesToPage(favoriteArticles,pageable);

    }

    private Page<Article> mapSetArticlesToPage(List<Article> articleSet, Pageable pageable) {
        List<Article> articleList = new ArrayList<>(articleSet);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), articleList.size());
        List<Article> subList = articleList.subList(start, end);

        return new PageImpl<>(subList, pageable, articleList.size());
    }

    @Override
    public Page<Article> findPublishedArticlesByTopicName(String topicName, Pageable pageable) {
        return articleRepository.findArticlesByPublishedIsTrueAndTopicsName(topicName, pageable);
    }

    @Override
    public Page<Article> findPublishedArticlesByUserTopicsOfInterest(Pageable pageable) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Set<Article> articles = new HashSet<>();
        for (Topic topic : authenticatedUser.getTopicsOfInterest()) {
            articles.addAll(findPublishedArticlesByTopicName(topic.getName(), pageable).getContent());
        }
       return mapSetArticlesToPage(List.copyOf(articles),pageable);
    }


    @Override
    public Page<Article> findPublishedArticlesByTitleIsContainingIgnoreCaseString(String title, Pageable pageable) {
        return articleRepository.findArticlesByPublishedIsTrueAndTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    public Page<Article> findNotPublishedArticlesByAuthenticationUser( Pageable pageable) {
        User authenticatedUser =customUserDetailsService.getAuthenticatedUser();
        return articleRepository.findArticlesByPublishedIsFalseAndUserUsername(authenticatedUser.getUsername(),pageable);
    }


    @Override
    public Article findPublishedArticleById(long articleId) {
        return articleRepository.findArticleByIdAndPublishedIsTrue(articleId).orElseThrow(() -> new ArticleNotFoundException("Article not found."));
    }

    @Override
    public Article findArticleById(long articleId) {
       return articleRepository.findById(articleId).orElseThrow(() -> new ArticleNotFoundException("Article not found."));
    }

    @Override
    public Article publishArticle(long articleId) {
        Article article = findArticleById(articleId);
        if (article.isPublished()) {
            throw new IllegalArgumentException("You are already saved this user.");
        }
        article.setPublished(true);
        return articleRepository.save(article);
    }

    @Override
    @Transactional
    public Article toggleFavoriteStatus(long articleId) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        Article article = findPublishedArticleById(articleId);
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
        Article article = findPublishedArticleById(articleId);
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
        Article article = findPublishedArticleById(articleId);
        return authenticatedUser.getFavoriteArticles().contains(article);
    }

    @Override
    public boolean isArticleLiked(long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        return authenticatedUser.getLikedArticles().contains(articleId);
    }

}