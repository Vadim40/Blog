package com.example.blog.Services;

import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Topic;
import com.example.blog.Models.User;
import com.example.blog.Repositories.TopicRepository;
import com.example.blog.Services.Implementations.CustomUserDetailsService;
import com.example.blog.Services.Implementations.TopicServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;


import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private TopicServiceImpl topicService;


    @Test
    public void updateTopic_ThrowException() {
        User user = new User();
        user.setRoles(Set.of(Role.ROLE_USER));
        Topic topic = new Topic();

        when(customUserDetailsService.getAuthenticatedUser()).thenReturn(user);

        Assertions.assertThatThrownBy(() -> topicService.updateTopicById(topic, 1L)).isInstanceOf(AccessDeniedException.class);
    }
}
