package com.example.blog.Services;

import com.example.blog.Excteptions.TopicAlreadyExists;
import com.example.blog.Excteptions.TopicNotFoundException;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Topic;
import com.example.blog.Models.User;
import com.example.blog.Repositories.TopicRepository;
import com.example.blog.Services.Interfaces.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Set;
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
   private final TopicRepository topicRepository;
   private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Set<Topic> findTopicsByNameContaining(String name) {
        return topicRepository.findTopicsByNameContaining(name);
    }

    @Override
    public Set<Topic> findAllTopics() {
      return (Set<Topic>) topicRepository.findAll();
    }

    @Override
    public Topic findTopicById(long topicId) {
        return topicRepository.findById(topicId).orElseThrow(()->
                new TopicNotFoundException("Topic not found"));
    }



    @Override
    public Topic saveTopic(Topic topic) {
        if(topicRepository.existsByName(topic.getName())){
            throw new TopicAlreadyExists("Topic Already exists");
        }
       return topicRepository.save(topic);
    }

    @Override
    public Topic updateTopicById(Topic topic, long topicId) {
       checkAccess();
       topic.setId(topicId);
      return topicRepository.save(topic);
    }

    @Override
    public void deleteTopicById(long topicId) {
        checkAccess();
       topicRepository.deleteById(topicId);
    }
    private  void checkAccess(){
        User authenticatedUser =customUserDetailsService.getAuthenticatedUser();
        if(!authenticatedUser.getRoles().contains(Role.ADMIN)){
            throw new AccessDeniedException("You don't have permission to perform this action on this topic ");
        }
    }
}
