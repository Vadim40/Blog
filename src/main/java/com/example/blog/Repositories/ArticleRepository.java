package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {


    Page<Article> findArticlesByPublishedIsTrueAndUserUsername(String username, Pageable pageable);


    Page<Article> findArticlesByPublishedIsTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);



    Page<Article> findArticlesByPublishedIsTrueAndTopicsName(String name, Pageable pageable);
    Page<Article> findArticlesByPublishedIsFalseAndUserUsername(String username, Pageable pageable);
    Optional<Article> findArticleByIdAndPublishedIsTrue(long articleId);


}