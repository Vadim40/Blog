package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import com.example.blog.Models.Comment;
import com.example.blog.Models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CommentRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Test
    void findCommentsByArticleId() {
        Article article =articleRepository.save( Article.builder()
                .text("Article")
                .build());
        articleRepository.save(article);
        Comment comment1=Comment.builder()
                .likes(10)
                .article(article)
                .build();
        Comment comment2=Comment.builder()
                .likes(20)
                .article(article)
                .build();
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        Set<Comment> foundComments=commentRepository.findCommentsByArticleId(article.getId());
        Assertions.assertThat(foundComments).isNotEmpty();
        Assertions.assertThat(foundComments.size()).isGreaterThan(1);
    }

    @Test
    void findCommentsByUserId() {
        User user = userRepository.save(User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .selfDescription("Test user")
                .build());


        Comment comment1=Comment.builder()
                .likes(10)
                .user(user)
                .build();
        Comment comment2=Comment.builder()
                .likes(20)
                .user(user)
                .build();
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        Set<Comment> foundComments=commentRepository.findCommentsByUserId(user.getId());
        Assertions.assertThat(foundComments).isNotEmpty();
        Assertions.assertThat(foundComments.size()).isGreaterThan(1);
    }

    @Test
    void findCommentsByParentCommentId() {
        Comment parentComment= commentRepository.save(Comment.builder()
                .likes(10)
                .build());
        Comment comment1=Comment.builder()
                .likes(2)
                .parentComment(parentComment)
                .build();
        Comment comment2=Comment.builder()
                .likes(3)
                .parentComment(parentComment)
                .build();
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        Set<Comment> foundComments=commentRepository.findCommentsByParentCommentId(parentComment.getId());
        Assertions.assertThat(foundComments).isNotEmpty();
        Assertions.assertThat(foundComments.size()).isGreaterThan(1);
    }
}