package com.example.blog.Services.Implementations;

import com.example.blog.Excteptions.TopicNotFoundException;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Topic;
import com.example.blog.Models.User;
import com.example.blog.Repositories.TopicRepository;
import com.example.blog.Services.Interfaces.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public List<Topic> findTopicsByNameIgnoreCaseContaining(String topicName) {
        return topicRepository.findTopicsByNameIgnoreCaseContaining(topicName);
    }

    @Override
    public int findTopicFollowersCount(String topicName) {
        Topic topic = topicRepository.findTopicByName(topicName);
        return topic.getInterestedUsers().size();
    }

    @Override
    public Topic findTopicByName(String topicName) {
        return topicRepository.findTopicByName(topicName);
    }

    @Override
    public List<Topic> findAllTopics() {
        return topicRepository.findAll();
    }

    @Override
    public Topic findTopicById(long topicId) {
        return topicRepository.findById(topicId).orElseThrow(() ->
                new TopicNotFoundException("Topic not found"));
    }


    @Override
    public Topic saveTopic(Topic topic) {
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

    @Override
    public boolean isTopicExists(String name) {
        return topicRepository.existsByNameIgnoreCase(name);
    }

    private void checkAccess() {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        if (!authenticatedUser.getRoles().contains(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You don't have permission to perform this action on this topic ");
        }
    }
}
