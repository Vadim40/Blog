package com.example.blog.Repositories;

import com.example.blog.Excteptions.UserNotFoundException;
import com.example.blog.Models.Article;
import com.example.blog.Models.Comment;
import com.example.blog.Models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void testFindUserByArticleId() {

        User user = User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .selfDescription("Test user")
                .build();
        userRepository.save(user);


        Article article1 = articleRepository.save(Article.builder()
                .text("Article 1")
                .user(user)
                .build());

        Article article2 = articleRepository.save(Article.builder()
                .text("Article 2")
                .user(user)
                .build());
        User foundUser1=userRepository.findUserByArticlesId(article1.getId()).orElseThrow(
                () -> new UserNotFoundException("User not Found"));
        User foundUser2=userRepository.findUserByArticlesId(article2.getId()).orElseThrow(
                () -> new UserNotFoundException("User not Found"));

        Assertions.assertThat(foundUser1).isEqualTo(foundUser2);
        Assertions.assertThat(foundUser1.getEmail()).isEqualTo(user.getEmail());

    }

    @Test
    void findUserByCommentsId() {
        User user = User.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .selfDescription("Test user")
                .build();
        userRepository.save(user);

        Comment comment1=commentRepository.save(Comment.builder()
                .likes(10)
                .user(user)
                .build());

        Comment comment2=commentRepository.save(Comment.builder()
                .likes(10)
                .user(user)
                .build());
        User foundUser1=userRepository.findUserByCommentsId(comment1.getId()).orElseThrow(
                () -> new UserNotFoundException("User not Found"));
        User foundUser2=userRepository.findUserByCommentsId(comment2.getId()).orElseThrow(
                () -> new UserNotFoundException("User not Found"));
        Assertions.assertThat(foundUser1).isEqualTo(foundUser2);
        Assertions.assertThat(foundUser1.getEmail()).isEqualTo(user.getEmail());
    }
}
