package com.example.blog.Repositories;

import com.example.blog.Models.Topic;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TopicRepositoryTest {
    @Autowired
    private TopicRepository topicRepository;

    @Test
    public void findTopicsByNameIgnoreCaseContaining() {
        Topic topic1 = Topic.builder()
                .name("java")
                .build();
        Topic topic2 = Topic.builder()
                .name("jazz")
                .build();
        Topic topic3 = Topic.builder()
                .name("Japan")
                .build();
        topicRepository.save(topic1);
        topicRepository.save(topic2);
        topicRepository.save(topic3);

        List<Topic> topics = topicRepository.findTopicsByNameIgnoreCaseContaining("ja");

        Assertions.assertThat(topics.size()).isEqualTo(3);
    }

    @Test
    public void findTopicsByName() {
        Topic topic1 = topicRepository.save(Topic.builder()
                .name("java")
                .build());
        topicRepository.save(Topic.builder()
                .name("jazz")
                .build());

        Topic foundTopic = topicRepository.findTopicByName("java");

        Assertions.assertThat(foundTopic).isEqualTo(topic1);
    }

    @Test
    public void existsByNameIgnoreCase() {
        topicRepository.save(Topic.builder()
                .name("java")
                .build());
        Assertions.assertThat(topicRepository.existsByNameIgnoreCase("Java")).isTrue();
    }

}
