package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Topic;

import java.util.Set;

public interface TopicService {
    Set<Topic> findTopicsByNameIgnoreCaseContaining(String topicName);
    int findTopicFollowersCount(String topicName);

    Topic findTopicByName(String topicName);
    Set<Topic> findAllTopics();

    Topic findTopicById(long topicId);

    Topic saveTopic(Topic topic);

    Topic updateTopicById(Topic topic, long topicId);

    void deleteTopicById(long topicId);
}
