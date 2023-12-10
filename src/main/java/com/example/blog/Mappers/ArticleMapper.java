package com.example.blog.Mappers;

import com.example.blog.Models.Article;
import com.example.blog.Models.DTOs.ArticleDTO;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {
    public ArticleDTO mapToDTO(Article article){

        return ArticleDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .creationDate(article.getCreationDate())
                .likes(article.getLikes())
                .text(article.getText())
                .build();
    }
    public Article mapToEntity(ArticleDTO articleDTO){
        return Article.builder()
                .id(articleDTO.getId())
                .creationDate(articleDTO.getCreationDate())
                .likes(articleDTO.getLikes())
                .text(articleDTO.getText())
                .build();
    }
}
