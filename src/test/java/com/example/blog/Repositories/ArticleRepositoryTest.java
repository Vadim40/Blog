package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import com.example.blog.Models.Comment;
import com.example.blog.Models.Topic;
import com.example.blog.Models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    public void testSaveArticle() {
        Article article2 = Article.builder()
                .text("Article 2")
                .build();

        Article savedArticle = articleRepository.save(article2);

        Assertions.assertThat(savedArticle).isNotNull();

    }



    @Test
    void findByTitleContaining() {
        Article article1 = Article.builder()
                .text("java is ...")
                .title("it's something about test and java")
                .published(true)
                .build();
        Article article2 = Article.builder()
                .text("about som")
                .title("it's something about birds")
                .published(true)
                .build();
        articleRepository.save(article1);
        articleRepository.save(article2);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Article> articles1 = articleRepository.
                findArticlesByTitleContainingIgnoreCase("Java", pageable);
        Page<Article> articles2 = articleRepository.
                findArticlesByTitleContainingIgnoreCase("something", pageable);
        Assertions.assertThat(articles1.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(articles2.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findArticleByUsername() {
        User user = userRepository.save(User.builder()
                .username("bad wolves")
                .build());


        Article article1 = Article.builder()
                .text("Article 1")
                .user(user)
                .published(true)
                .build();

        Article article2 = Article.builder()
                .text("Article 2")
                .published(true)
                .user(user)
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Article> articles = articleRepository.findArticlesByUser_Username(user.getUsername(), pageable);

        Assertions.assertThat(articles.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void findArticleByTopicName() {

        Topic topic=topicRepository.save(new Topic());
        topic.setName("Java");
        Article article1 = Article.builder()
                .topics(new HashSet<>(Arrays.asList(topic)))
                .text("Article 1")
                .published(true)
                .build();

        Article article2 = Article.builder()
                .text("Article 2")
                .topics(new HashSet<>(Arrays.asList(topic)))
                .published(true)
                .build();
        articleRepository.save(article1);
        articleRepository.save(article2);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Article> articles = articleRepository.findArticlesByTopicsName("Java",pageable);
        Assertions.assertThat(articles.getTotalElements()).isEqualTo(2);
    }

}