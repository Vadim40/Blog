package com.example.blog.Mappers;

import com.example.blog.Models.Article;
import com.example.blog.Models.DTOs.ArticleDTO;
import com.example.blog.Models.DTOs.ArticleViewDTO;
import com.example.blog.Models.DTOs.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ArticleMapper {
    private final ImageMapper imageMapper;
    private final TopicMapper topicMapper;

    public ArticleDTO mapToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setCreationDate(article.getCreationDate());
        articleDTO.setImages(article.getImages().stream().map(imageMapper::mapToDTO).collect(Collectors.toList()));
        articleDTO.setTopics(article.getTopics().stream().map(topicMapper::mapToDTO).collect(Collectors.toList()));
        articleDTO.setLikes(article.getLikes());
        articleDTO.setText(article.getText());
        return articleDTO;
    }

    public Article mapToEntity(ArticleDTO articleDTO) {
        Article article = new Article();
        article.setId(articleDTO.getId());
        article.setCreationDate(articleDTO.getCreationDate());
        article.setImages(articleDTO.getImages().stream().map(imageMapper::mapToEntity).collect(Collectors.toList()));
        article.setTopics(articleDTO.getTopics().stream().map(topicMapper::mapToEntity).collect(Collectors.toSet()));
        article.setLikes(articleDTO.getLikes());
        article.setText(articleDTO.getText());
        return article;
    }
}
