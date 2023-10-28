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


import java.util.HashSet;
import java.util.List;
import java.util.Set;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void testSaveArticle() {
        Article article2 = Article.builder()
                .text("Article 2")
                .build();

        Article savedArticle = articleRepository.save(article2);

        Assertions.assertThat(savedArticle).isNotNull();

    }

    @Test
    public void testFindArticlesByUserId() {

        User user = userRepository.save(User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .selfDescription("Test user")
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


        Pageable pageable = PageRequest.of(0, 10);
        Page<Article> articlesPage = articleRepository.findArticlesByUserId(user.getId(), pageable);


        List<Article> articles = articlesPage.getContent();
        Assertions.assertThat(articles).isNotEmpty();

    }

    @Test
    void findArticleByCommentsId() {
        Article article = Article.builder()
                .text("Article")
                .published(true)
                .build();
        articleRepository.save(article);
        Comment comment1 = commentRepository.save(Comment.builder()
                .likes(10)
                .article(article)
                .build());
        Comment comment2 = commentRepository.save(Comment.builder()
                .likes(20)
                .article(article)
                .build());
        Article foundArticle1 = articleRepository.findArticleByCommentsId(comment1.getId());
        Article foundArticle2 = articleRepository.findArticleByCommentsId(comment2.getId());


        Assertions.assertThat(foundArticle1).isNotNull();
        Assertions.assertThat(foundArticle1).isEqualTo(foundArticle2);
        Assertions.assertThat(comment1.getId()).isNotEqualTo(comment2.getId());
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
    void findArticlesByTopic(){
        Topic topic1=topicRepository.save(Topic.builder()
                .articles(new HashSet<>())
                .name("java")
                .build());
        Topic topic2=topicRepository.save(Topic.builder()
                .articles(new HashSet<>())
                .name("job")
                .build());
        Topic topic3=topicRepository.save(Topic.builder()
                .articles(new HashSet<>())
                .name("study")
                .build());

        Set<Topic> topics1=new HashSet<>();
        topics1.add(topic1);
        topics1.add(topic2);
        Set<Topic> topics2=new HashSet<>();
        topics2.add(topic1);
        topics2.add(topic3);
        Article article1=Article.builder()
                .topics(topics1)
                .published(true)
                .build();
        Article article2=Article.builder()
                .topics(topics2)
                .published(true)
                .build();
        articleRepository.save(article1);
        articleRepository.save(article2);

        Pageable pageable=PageRequest.of(0,3);
        Page<Article> pageOfJavaTopic=articleRepository.findArticlesByTopicsId(topic1.getId(),pageable);
        Page<Article> pageOfJobTopic=articleRepository.findArticlesByTopicsId(topic2.getId(),pageable);

        Assertions.assertThat(pageOfJavaTopic.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(pageOfJobTopic.getTotalElements()).isEqualTo(1);
    }

}