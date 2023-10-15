package com.example.blog.Repositories;

import com.example.blog.Models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Set<Topic> findTopicsByNameContaining(String name);


    boolean existsByName(String name);
}
