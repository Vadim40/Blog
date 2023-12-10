package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Article;
import com.example.blog.Models.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;


import java.util.Set;

public interface ArticleService {


    Page<Article> findArticlesByUser_Username(String username, int pageSize, int pageNumber);

    Set<Article> findFavoriteArticlesByAuthenticationUser();

    Page<Article> findArticlesByTopicName(String topicName, int pageSize, int pageNumber);

    Page<Article> findArticlesByUserTopicsOfInterest(int pageSize, int pageNumber);

    Page<Article> findArticlesByTitleIsContainingIgnoreCaseString(String title, int pageSize, int pageNumber);

    Page<Article> findArticlesBySorting(int pageSize, int pageNumber, Sort.Direction direction, String sortBy);



    Article findArticleById(long articleId);

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
