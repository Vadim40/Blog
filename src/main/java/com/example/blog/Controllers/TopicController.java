package com.example.blog.Controllers;

import com.example.blog.Mappers.TopicMapper;
import com.example.blog.Models.DTOs.ValidationErrorResponse;
import com.example.blog.Models.Topic;
import com.example.blog.Models.DTOs.TopicDTO;
import com.example.blog.Services.Implementations.TopicServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topics")
public class TopicController {
    private final TopicServiceImpl topicService;
    private final TopicMapper topicMapper;

    @GetMapping
    public ResponseEntity<List<TopicDTO>> getAllTopics() {
        List<Topic> topics = topicService.findAllTopics();
        if (topics.isEmpty()) {
            log.warn("no topics found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<TopicDTO> topicDTOS = topics.stream().map(topicMapper::mapToTopicDTO).collect(Collectors.toList());
        return new ResponseEntity<>(topicDTOS, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TopicDTO>> getTopicsByNameContaining(
            @RequestParam String name) {
        List<Topic> topics = topicService.findTopicsByNameIgnoreCaseContaining(name);
        if (topics.isEmpty()) {
            log.warn("no topics found by this name: {}", name);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<TopicDTO> topicDTOS = topics.stream().map(topicMapper::mapToTopicDTO).collect(Collectors.toList());
        return new ResponseEntity<>(topicDTOS, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createTopic(@RequestBody @Valid TopicDTO topicDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ValidationErrorResponse errorResponse=new ValidationErrorResponse(bindingResult);
            log.warn("validation errors to creating topic: {}", errorResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        Topic topic = topicMapper.mapToTopic(topicDTO);
        topicService.saveTopic(topic);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{topicId}/update")
    public ResponseEntity<Object> updateTopic(
            @PathVariable long topicId,
            @RequestBody  @Valid TopicDTO topicDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ValidationErrorResponse errorResponse=new ValidationErrorResponse(bindingResult);
            log.warn("validation errors to creating topic: {}", errorResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        Topic topic = topicMapper.mapToTopic(topicDTO);
        topicService.updateTopicById(topic, topicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{topicId}/delete")
    public ResponseEntity<Void> deleteTopic(@PathVariable long topicId) {
        topicService.deleteTopicById(topicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
