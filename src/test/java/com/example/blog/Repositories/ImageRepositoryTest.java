package com.example.blog.Repositories;

import com.example.blog.Models.Article;
import com.example.blog.Models.Image;
import com.example.blog.Models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ImageRepositoryTest {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findImagesByArticleId() {
        User user = userRepository.save(User.builder()
                .username("bad wolves")
                .build());
        Article article = articleRepository.save(Article.builder()
                .published(true)
                .user(user)
                .build());

        Image image1 = Image.
                builder()
                .article(article)
                .build();
        imageRepository.save(image1);

        List<Image> foundImages = imageRepository.findImagesByArticleId(article.getId());

        Assertions.assertThat(foundImages.size()).isEqualTo(1);
    }

    @Test
    void findImageByUserId() {

        User user = userRepository.save(User.builder()
                .id(1L)
                .build());
        Image image = Image.builder()
                .user(user)
                .build();
        imageRepository.save(image);
        Optional<Image> foundImageOptional = imageRepository.findImageByUserId(user.getId());

        Assertions.assertThat(foundImageOptional)
                .isPresent()
                .hasValue(image);
    }
}
