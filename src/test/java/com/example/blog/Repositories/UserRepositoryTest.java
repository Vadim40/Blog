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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

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
                .published(true)
                .user(user)
                .build());

        Article article2 = articleRepository.save(Article.builder()
                .text("Article 2")
                .published(true)
                .user(user)
                .build());
        User foundUser1 = userRepository.findUserByArticlesId(article1.getId()).orElseThrow(
                () -> new UserNotFoundException("User not Found"));
        User foundUser2 = userRepository.findUserByArticlesId(article2.getId()).orElseThrow(
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

        Comment comment1 = commentRepository.save(Comment.builder()
                .likes(10)
                .user(user)
                .build());

        Comment comment2 = commentRepository.save(Comment.builder()
                .likes(10)
                .user(user)
                .build());
        User foundUser1 = userRepository.findUserByCommentsId(comment1.getId()).orElseThrow(
                () -> new UserNotFoundException("User not Found"));
        User foundUser2 = userRepository.findUserByCommentsId(comment2.getId()).orElseThrow(
                () -> new UserNotFoundException("User not Found"));
        Assertions.assertThat(foundUser1).isEqualTo(foundUser2);
        Assertions.assertThat(foundUser1.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void findUsersByUsername() {
        userRepository.save(User.builder()
                .username("around")
                .build());
        userRepository.save(User.builder()
                .username("Arron")
                .build());
        Pageable pageable = PageRequest.of(0, 3);
        Page<User> users = userRepository.findUsersByUsernameIsContainingIgnoreCase("ar", pageable);
        Assertions.assertThat(users.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findFollowersByUser() {
        User user1 = userRepository.save(User.builder()
                .username("around")
                .build());
        User user2 = userRepository.save(User.builder()
                .username("some")
                .build());
        List<User> followers = new ArrayList<>();
        followers.add(user1);
        followers.add(user2);
        User user3 = userRepository.save(User.builder()
                .username("Arron")
                .followers(followers)
                .build());
        User user = userRepository.findUserByUsername(user3.getUsername()).orElseThrow(null);
        Assertions.assertThat(user.getFollowers().size()).isEqualTo(2);
    }

    @Test
    void findFollowingsByUser() {
        User user1 = new User();
        user1.setUsername("Arron");
        User user3 = new User();
        user1.getFollowing().add(user3);
        user3.getFollowers().add(user1);
        userRepository.save(user3);
        userRepository.save(user1);
        User user = userRepository.findUserByUsername(user1.getUsername()).orElseThrow(() -> new RuntimeException("not found"));
        Assertions.assertThat(user.getFollowing().size()).isEqualTo(1);
    }


}
