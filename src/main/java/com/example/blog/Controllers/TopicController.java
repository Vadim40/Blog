package com.example.blog.Controllers;

import com.example.blog.Mappers.TopicMapper;
import com.example.blog.Models.Topic;
import com.example.blog.Models.DTOs.TopicDTO;
import com.example.blog.Services.Impementations.TopicServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/topics")
public class TopicController {
    private final TopicServiceImpl topicService;
    private final TopicMapper topicMapper;

    @GetMapping("")
    public String getAllTopics(Model model) {
        Set<Topic> topics = topicService.findAllTopics();
        model.addAttribute("topics", topics);
        return "topics";
    }

    @GetMapping("/search/topic")
    public String getTopicsByNameContaining(
            @RequestParam String name,
            Model model) {
        Set<Topic> topics = topicService.findTopicsByNameIgnoreCaseContaining(name);
        model.addAttribute("topics", topics);
        return "allTopics";
    }

    @PutMapping("/{topicId}/update")
    public String updateTopic(
            @PathVariable long topicId,
            @RequestBody TopicDTO topicDTO) {
        Topic topic = topicMapper.mapToEntity(topicDTO);
        topicService.updateTopicById(topic, topicId);
        return "some";
    }

    @DeleteMapping("/{topicId}/delete")
    public String deleteTopic(@PathVariable long topicId) {
        topicService.deleteTopicById(topicId);
        return "some";
    }
}
