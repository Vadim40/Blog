package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import com.example.blog.Models.Comment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;




@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CommentRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findCommentsByParentCommentIsNullArticleId() {
        Article article = articleRepository.save(Article.builder()
                .text("Article")
                .build());
        articleRepository.save(article);
        Comment comment1 = Comment.builder()
                .likes(10)
                .article(article)
                .build();
        Comment comment2 = Comment.builder()
                .likes(20)
                .article(article)
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
        Comment parentComment = commentRepository.save(Comment.builder()
                .likes(10)
                .build());
        Comment comment1 = Comment.builder()
                .likes(2)
                .parentComment(parentComment)
                .build();
        Comment comment2 = Comment.builder()
                .likes(3)
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