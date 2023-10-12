package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.Set;

public interface IArticleService {
    Page<Article> findArticlesByUserId(long userId, int size, int page);

    Set<Article> findSavedArticlesByAuthenticationUser();


//    Page<Article> findArticlesByTags(Set<String> tags, int size, int page);
//
//    Page<Article> findArticlesByAllTags(Set<String> tags, int size, int page);

    Page<Article> findArticlesByTitleIsContainingIgnoreCaseString(String title, int size, int page);

    Page<Article> findArticlesBySorting(int size, int page, Sort.Direction direction, String sortBy);



    Article findArticleByCommentId(long commentId);

    Article findArticleById(long articleId);

    Article saveArticle(Article article);

    Article updateArticleById(Article article, long articleId);
    void addToSavedUserArticles(long articleId);

    void removeFromSavedUserArticles(long articleId);
    void deleteArticleById(long id);

    void putLike(long articleId);

    void removeLike(long articleId);
}
