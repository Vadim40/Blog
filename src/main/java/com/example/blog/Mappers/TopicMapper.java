package com.example.blog.Mappers;

import com.example.blog.Models.DTOs.TopicDTO;
import com.example.blog.Models.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {
   public TopicDTO mapToDTO(Topic topic){
       return TopicDTO.builder()
               .id(topic.getId())
               .name(topic.getName())
               .build();
   }
   public Topic mapToEntity(TopicDTO topicDTO){
       return Topic.builder()
               .id(topicDTO.getId())
               .name(topicDTO.getName())
               .build();
   }
}
