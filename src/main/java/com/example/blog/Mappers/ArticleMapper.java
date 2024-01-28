package com.example.blog.Mappers;

import com.example.blog.Models.Article;
import com.example.blog.Models.DTOs.ArticleDTO;
import com.example.blog.Models.DTOs.ArticleViewDTO;
import com.example.blog.Models.DTOs.UserDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ArticleMapper {
    private final ImageMapper imageMapper = new ImageMapper();
    private final TopicMapper topicMapper = new TopicMapper();


    public ArticleDTO mapToArticleDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setCreationDate(article.getCreationDate());
        articleDTO.setImages(article.getImages().stream().map(imageMapper::mapToImageDTO).collect(Collectors.toList()));
        articleDTO.setTopics(article.getTopics().stream().map(topicMapper::mapToTopicDTO).collect(Collectors.toList()));
        articleDTO.setLikes(article.getLikes());
        articleDTO.setText(article.getText());
        return articleDTO;
    }

    public Article mapToArticle(ArticleDTO articleDTO) {
        Article article = new Article();
        article.setId(articleDTO.getId());
        article.setCreationDate(articleDTO.getCreationDate());
        article.setImages(articleDTO.getImages().stream().map(imageMapper::mapToImage).collect(Collectors.toList()));
        article.setTopics(articleDTO.getTopics().stream().map(topicMapper::mapToTopic).collect(Collectors.toSet()));
        article.setLikes(articleDTO.getLikes());
        article.setText(articleDTO.getText());
        return article;
    }

    public ArticleViewDTO mapToArticleViewDTO(Article article, boolean isArticleFavorite, boolean isArticleLiked) {
        UserMapper userMapper = new UserMapper();
        UserDTO userDTO = userMapper.mapToUserDTO(article.getUser());
        ArticleViewDTO articleViewDTO = new ArticleViewDTO();
        articleViewDTO.setArticleDTO(mapToArticleDTO(article));
        articleViewDTO.getUserProfileDTO().setAvatar(userDTO.getAvatar());
        articleViewDTO.getUserProfileDTO().setUsername(userDTO.getUsername());
        articleViewDTO.setFavorite(isArticleFavorite);
        articleViewDTO.setLiked(isArticleLiked);

        return articleViewDTO;
    }
}
