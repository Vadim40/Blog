package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Article;
import com.example.blog.Models.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {


    Page<Article> findPublishedArticlesByUserUsername(String username, Pageable pageable);

    Page<Article> findPublishedFavoriteArticlesByAuthenticationUser(Pageable pageable);

    Page<Article> findPublishedArticlesByTopicName(String topicName, Pageable pageable);

    Page<Article> findPublishedArticlesByUserTopicsOfInterest(Pageable pageable);

    Page<Article> findPublishedArticlesByTitleIsContainingIgnoreCaseString(String title, Pageable pageable);
    Page<Article> findNotPublishedArticlesByAuthenticationUser(Pageable pageable);

    Article findPublishedArticleById(long articleId);

    Article publishArticle(long articleId);

    Article saveArticle(Article article);

    Article saveImageToArticle(Image image, long articleId);

    Article updateArticleById(Article article, long articleId);

    Article toggleFavoriteStatus(long articleId);

    void deleteArticleById(long id);

    Article toggleLikeStatus(long articleId);

    boolean isArticleFavorite(long articleId);

    boolean isArticleLiked(long articleId);

}