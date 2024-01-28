package com.example.blog.Mappers;

import com.example.blog.Models.DTOs.TopicDTO;
import com.example.blog.Models.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {
    public TopicDTO mapToTopicDTO(Topic topic) {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setId(topic.getId());
        topicDTO.setName(topic.getName());
        return topicDTO;
    }

    public Topic mapToTopic(TopicDTO topicDTO) {
        Topic topic = new Topic();
        topic.setId(topicDTO.getId());
        topic.setName(topicDTO.getName());
        return topic;
    }
}
