package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findArticlesByUserId(long userId, Pageable pageable);

    Article findArticleByCommentsId(long commentId);

    Page<Article> findArticlesByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Article> findArticlesByTopicsId(long topicId, Pageable pageable);


}