package com.example.blog.Mappers;

import com.example.blog.Models.DTOs.TopicDTO;
import com.example.blog.Models.Topic;

public class TopicMapper {
   public TopicDTO toDTO(Topic topic){
       return TopicDTO.builder()
               .id(topic.getId())
               .name(topic.getName())
               .build();
   }
   public Topic toEntity(TopicDTO topicDTO){
       return Topic.builder()
               .id(topicDTO.getId())
               .name(topicDTO.getName())
               .build();
   }
}
