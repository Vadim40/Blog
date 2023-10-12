package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findArticlesByUserId(long id, Pageable pageable);

    Article findArticleByCommentsId(long id);


    Page<Article> findArticlesByTitleContainingIgnoreCase(String title, Pageable pageable);

//    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t IN :tags")
//    Page<Article> findByTags(@Param("tags") Set<String> tags, Pageable pageable);
//
//    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t IN :tags GROUP BY a HAVING COUNT(DISTINCT t) = :tagCount")
//    Page<Article> findByAllTags(@Param("tags") Set<String> tags, @Param("tagCount") int tagCount, Pageable pageable);

}