package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Category;
import com.example.blog.Models.Topic;
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
    @Query("SELECT a from Article a JOIN a.topics t WHERE t=:topic")
    Page<Article> findArticlesByTopic(@Param("topic") Topic topic, Pageable pageable);
}