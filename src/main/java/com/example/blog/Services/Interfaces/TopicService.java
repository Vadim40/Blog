package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Topic;

import java.util.List;

public interface TopicService {
    List<Topic> findTopicsByNameIgnoreCaseContaining(String topicName);

    int findTopicFollowersCount(String topicName);

    Topic findTopicByName(String topicName);

    List<Topic> findAllTopics();

    Topic findTopicById(long topicId);

    Topic saveTopic(Topic topic);

    Topic updateTopicById(Topic topic, long topicId);

    void deleteTopicById(long topicId);
    boolean isTopicExists(String name);
}
