package com.example.blog.Controllers;

import com.example.blog.Excteptions.ArticleNotFoundException;
import com.example.blog.Mappers.ArticleMapper;
import com.example.blog.Mappers.UserMapper;
import com.example.blog.Models.Article;
import com.example.blog.Services.Implementations.ArticleServiceImpl;
import com.example.blog.Services.Implementations.TopicServiceImpl;
import com.example.blog.Utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ArticleControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleServiceImpl articleService;
    @MockBean
    private ArticleMapper articleMapper;
    @MockBean
    private TopicServiceImpl topicService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void findArticlesByTopic_StatusOk() throws Exception {

        Article mockArticle = new Article();
        mockArticle.setId(1L);
        mockArticle.setTitle("Test Article");
        when(articleService.findPublishedArticlesByTopicName(eq("exampleTopic"), any(Pageable.class)))
                .thenReturn(createMockArticlePage());
        when(topicService.findTopicFollowersCount(eq("exampleTopic")))
                .thenReturn(10);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/topic/{topicName}", "exampleTopic")
                        .param("pageSize", "20")
                        .param("pageNumber", "1")
                        .param("direction", "ASC")
                        .param("sortBy", "creationDate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findArticlesByTopic_StatusNoContent() throws Exception {
        Article mockArticle = new Article();
        mockArticle.setId(1L);
        mockArticle.setTitle("Test Article");
        when(articleService.findPublishedArticlesByTopicName(eq("exampleTopic"), any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/topic/{topicName}", "exampleTopic")
                        .param("pageSize", "20")
                        .param("pageNumber", "1")
                        .param("direction", "ASC")
                        .param("sortBy", "creationDate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testFindArticleByIdThrowsNotFoundException() throws Exception {
        long nonExistingArticleId = 1L;

        when(articleService.findPublishedArticleById(anyLong())).thenThrow(new ArticleNotFoundException("not found"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/articles/{articleId}/", nonExistingArticleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    void testToggleLikeStatus_Verify() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/articles/{articleId}/toggle-like", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(articleService).toggleLikeStatus(anyLong());

    }

    @Test
    @WithMockUser
    void testCreateArticle_BadRequest() throws Exception {
        Article article = new Article();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/articles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(article)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testDeleteArticle() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/articles/{articleId}/delete", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }



    private Page<Article> createMockArticlePage() {
        Article mockArticle = new Article();
        return new PageImpl<>(Collections.singletonList(mockArticle), createPageable(1, 20, Sort.Direction.ASC, "creationDate"), 1);
    }

    private Pageable createPageable(int pageNumber, int pageSize, Sort.Direction direction, String sortBy) {
        return PageRequest.of(pageNumber - 1, pageSize, direction, sortBy);
    }
}
