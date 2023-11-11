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

import java.util.List;
import java.util.Optional;

@DataJpaTest
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

        List<Image> images = List.of(new Image(), new Image(), new Image());
        Article article = articleRepository.save(Article.builder()
                .id(1L)
                .published(true)
                .images(images)
                .build());

        Image image1 = Image.
                builder()
                .article(article)
                .build();
        imageRepository.save(image1);

        List<Image> foundImages = imageRepository.findImagesByArticleId(1L);

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
