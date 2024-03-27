package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import com.example.blog.Models.Comment;
import com.example.blog.Models.User;
import org.antlr.v4.runtime.misc.LogManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CommentRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findCommentsByParentCommentIsNullArticleId() {
        User user = userRepository.save(User.builder()
                .username("bad wolves")
                .build());
        Article article = articleRepository.save(Article.builder()
                .text("Article")
                .user(user)
                .build());
        articleRepository.save(article);
        Comment comment1 = Comment.builder()
                .likes(10)
                .article(article)
                .user(user)
                .build();
        Comment comment2 = Comment.builder()
                .likes(20)
                .article(article)
                .user(user)
                .build();
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Comment> foundComments = commentRepository.findCommentsByParentCommentIsNullAndArticleId(article.getId(), pageable);
        Assertions.assertThat(foundComments).isNotEmpty();
        Assertions.assertThat(foundComments.getTotalElements()).isGreaterThan(1);
    }

    @Test
    void findCommentsByParentCommentId() {
        User user = userRepository.save(User.builder()
                .username("bad wolves")
                .build());
        Article article = articleRepository.save(Article.builder()
                .text("Article")
                .user(user)
                .build());
        Comment parentComment = commentRepository.save(Comment.builder()
                .likes(10)
                .article(article)
                .user(user)
                .build());
        Comment comment1 = Comment.builder()
                .likes(2)
                .article(article)
                .user(user)
                .parentComment(parentComment)
                .build();
        Comment comment2 = Comment.builder()
                .likes(3)
                .article(article)
                .user(user)
                .parentComment(parentComment)
                .build();
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Comment> foundComments = commentRepository.findCommentsByParentCommentId(parentComment.getId(), pageable);
        Assertions.assertThat(foundComments).isNotEmpty();
        Assertions.assertThat(foundComments.getTotalElements()).isGreaterThan(1);
    }
}