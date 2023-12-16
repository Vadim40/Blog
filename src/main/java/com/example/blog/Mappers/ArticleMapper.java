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
  private final    ImageMapper imageMapper;
  private final TopicMapper topicMapper;
    public ArticleDTO mapToDTO(Article article){

        return ArticleDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .creationDate(article.getCreationDate())
                .images(article.getImages().stream().map(imageMapper::mapToDTO).collect(Collectors.toList()))
                .topics(article.getTopics().stream().map(topicMapper::mapToDTO).collect(Collectors.toSet()))
                .likes(article.getLikes())
                .text(article.getText())
                .build();
    }
    public Article mapToEntity(ArticleDTO articleDTO){
        return Article.builder()
                .id(articleDTO.getId())
                .creationDate(articleDTO.getCreationDate())
                .images(articleDTO.getImages().stream().map(imageMapper::mapToEntity).collect(Collectors.toList()))
                .topics(articleDTO.getTopics().stream().map(topicMapper::mapToEntity).collect(Collectors.toSet()))
                .likes(articleDTO.getLikes())
                .text(articleDTO.getText())
                .build();
    }
}
