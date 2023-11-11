package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Article;
import com.example.blog.Models.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.Set;

public interface ArticleService {
    Page<Article> findArticlesByUserId(long userId, int size, int page);

    Set<Article> findSavedArticlesByAuthenticationUser();


    Page<Article> findArticlesByTopic(long topicId, int size, int page);

    Page<Article> findArticlesByUserTopicOfInterest(int size, int page);

    Page<Article> findArticlesByTitleIsContainingIgnoreCaseString(String title, int size, int page);

    Page<Article> findArticlesBySorting(int size, int page, Sort.Direction direction, String sortBy);



    Article findArticleByCommentId(long commentId);

    Article findArticleById(long articleId);

    Article publishArticle(Article article);

    Article  saveArticle(Article article);

    void saveImageToArticle(Image image, long articleId);

    Article updateArticleById(Article article, long articleId);
    void addToSavedUserArticles(long articleId);

    void removeFromSavedUserArticles(long articleId);
    void deleteArticleById(long id);

    void putLike(long articleId);

    void removeLike(long articleId);
}
