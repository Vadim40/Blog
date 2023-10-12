package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Category;
import com.example.blog.Models.Comment;
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
                .build();

        Article article2 = Article.builder()
                .text("Article 2")
                .user(user)
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);


        Pageable pageable = PageRequest.of(0, 10); // Настройте пагинацию по вашему усмотрению
        Page<Article> articlesPage = articleRepository.findArticlesByUserId(user.getId(), pageable);


        List<Article> articles = articlesPage.getContent();
        Assertions.assertThat(articles).isNotEmpty();

    }

    @Test
    void findArticleByCommentsId() {
        Article article = Article.builder()
                .text("Article")
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

//    @Test
//    void findByCategory() {
//        Article article1 = Article.builder()
//                .text("test")
//                .category(Category.ART_DESIGN)
//                .build();
//        Article article2 = Article.builder()
//                .text("sublime")
//                .category(Category.ART_DESIGN)
//                .build();
//        Article article3 = Article.builder()
//                .text("java")
//                .category(Category.TECHNOLOGY)
//                .build();
//        articleRepository.save(article1);
//        articleRepository.save(article2);
//        articleRepository.save(article3);
//        Pageable pageable = PageRequest.of(0, 3);
//        Page<Article> articles = articleRepository.findByCategory(Category.ART_DESIGN, pageable);
//        Assertions.assertThat(articles.getTotalElements()).isEqualTo(2);
//    }

    @Test
    void findByTitleContaining() {
        Article article1 = Article.builder()
                .text("java is ...")
                .title("it's something about test and java")
                .build();
        Article article2 = Article.builder()
                .text("about som")
                .title("it's something about birds")
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

//    @Test
//    void findArticlesByTags() {
//        Set<String> tags1 = new HashSet<>();
//        tags1.add("java");
//        tags1.add("Dto");
//        Set<String> tags2 = new HashSet<>();
//        tags2.add("java");
//        tags2.add("DTO");
//        tags2.add("mapper");
//        Set<String> find =new HashSet<>();
//        find.add("java");
//        find.add("C#");
//        Article article1 = Article.builder()
//                .tags(tags1)
//                .build();
//        Article article2 = Article.builder()
//                .tags(tags2)
//                .build();
//
//        articleRepository.save(article1);
//        articleRepository.save(article2);
//        Pageable pageable = PageRequest.of(0, 3);
//
//        Page<Article> articles=articleRepository.findByTags(find,pageable);
//
//        Assertions.assertThat(articles.getTotalElements()).isEqualTo(2);
//    } @Test
//    void findArticlesByAllTags() {
//        Set<String> tags1 = new HashSet<>();
//        tags1.add("java");
//        tags1.add("Dto");
//        Set<String> tags2 = new HashSet<>();
//        tags2.add("java");
//        tags2.add("DTO");
//        tags2.add("mapper");
//        Set<String> find =new HashSet<>();
//        find.add("java");
//        find.add("mapper");
//        Article article1 = Article.builder()
//                .tags(tags1)
//                .build();
//        Article article2 = Article.builder()
//                .tags(tags2)
//                .build();
//
//        articleRepository.save(article1);
//        articleRepository.save(article2);
//        Pageable pageable = PageRequest.of(0, 3);
//
//        Page<Article> articles=articleRepository.findByAllTags(find,find.size(),pageable);
//
//        Assertions.assertThat(articles.getTotalElements()).isEqualTo(1);
//    }
}