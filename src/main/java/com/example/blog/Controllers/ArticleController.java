package com.example.blog.Controllers;

import com.example.blog.Mappers.ArticleMapper;
import com.example.blog.Mappers.UserMapper;
import com.example.blog.Models.Article;
import com.example.blog.Models.DTOs.ArticleDTO;
import com.example.blog.Models.DTOs.ArticleViewDTO;
import com.example.blog.Models.DTOs.UserDTO;
import com.example.blog.Services.Implementations.ArticleServiceImpl;
import com.example.blog.Services.Implementations.TopicServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleServiceImpl articleService;
    private final TopicServiceImpl topicService;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    @GetMapping("/topic/{topicName}")
    public ResponseEntity<Object> findArticlesByTopic(
            @PathVariable String topicName,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "creationDate") String sortBy) {
        @AllArgsConstructor
        @Getter
        class CustomApiResponse {
            private final Page<ArticleViewDTO> articles;
            private final long topicSubscribers;
        }
        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findPublishedArticlesByTopicName(topicName, pageable);
        if (articles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToArticleViewDTO);
        CustomApiResponse customApiResponse = new CustomApiResponse(articleViewDTOPage,
                topicService.findTopicFollowersCount(topicName));
        return new ResponseEntity<>(customApiResponse, HttpStatus.OK);
    }


    @GetMapping("/user/{username}")
    public ResponseEntity<Page<ArticleViewDTO>> findArticlesByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "creationDate") String sortBy) {

        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findPublishedArticlesByUserUsername(username, pageable);
        if (articles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToArticleViewDTO);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }

    @GetMapping("/favorites")
    public ResponseEntity<Page<ArticleViewDTO>> findFavoriteArticles(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "creationDate") String sortBy) {
        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findPublishedFavoriteArticlesByAuthenticationUser(pageable);
        if (articles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToArticleViewDTO);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }

    @GetMapping("/draft")
    public ResponseEntity<Page<ArticleViewDTO>> findDraftArticles(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "creationDate") String sortBy) {
        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findNotPublishedArticlesByAuthenticationUser(pageable);
        if (articles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToArticleViewDTO);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }


    @GetMapping("/interests")
    public ResponseEntity<Page<ArticleViewDTO>> findArticlesByUserTopicOfInterest(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "creationDate") String sortBy) {
        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findPublishedArticlesByUserTopicsOfInterest(pageable);
        if (articles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToArticleViewDTO);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<ArticleViewDTO>> findArticlesByTitleIsContainingIgnoreCase(
            @RequestParam String title,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "creationDate") String sortBy) {
        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findPublishedArticlesByTitleIsContainingIgnoreCaseString(title, pageable);
        if (articles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToArticleViewDTO);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleViewDTO> findArticleById(@PathVariable long articleId) {
        Article article = articleService.findPublishedArticleById(articleId);
        ArticleViewDTO articleViewDTO = mapArticleToArticleViewDTO(article);
        return new ResponseEntity<>(articleViewDTO, HttpStatus.OK);
    }


    @PutMapping("/{articleId}/publish")
    public ResponseEntity<Void> publishArticle(@PathVariable long articleId) {
        articleService.publishArticle(articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createArticle(@RequestBody @Valid ArticleDTO articleDTO,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        Article article = articleMapper.mapToEntity(articleDTO);
        article = articleService.saveArticle(article);
        ArticleDTO savedArticleDTO = articleMapper.mapToDTO(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticleDTO);
    }

    @PutMapping("/{articleId}/update")
    public ResponseEntity<Object> updateArticle(
            @PathVariable long articleId,
            @RequestBody @Valid ArticleDTO articleDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        Article article = articleMapper.mapToEntity(articleDTO);
        articleService.updateArticleById(article, articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{articleId}/toggle-favorite")
    public ResponseEntity<Void> toggleFavoriteStatus(@PathVariable long articleId) {
        articleService.toggleFavoriteStatus(articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{articleId}/toggle-like")
    public ResponseEntity<Void> toggleLikeStatus(@PathVariable long articleId) {
        articleService.toggleLikeStatus(articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{articleId}/delete")
    public ResponseEntity<Void> deleteArticle(@PathVariable long articleId) {
        articleService.deleteArticleById(articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ArticleViewDTO mapArticleToArticleViewDTO(Article article) {
        ArticleDTO articleDTO = articleMapper.mapToDTO(article);
        UserDTO userDTO = userMapper.mapToDTO(article.getUser());

        ArticleViewDTO articleViewDTO = new ArticleViewDTO();
        articleViewDTO.setArticleDTO(articleDTO);
        articleViewDTO.setUserDTO(userDTO);
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            articleViewDTO.setFavorite(articleService.isArticleFavorite(article.getId()));
            articleViewDTO.setLiked(articleService.isArticleLiked(article.getId()));
        }
        return articleViewDTO;
    }

    private Pageable createPageable(int pageNumber, int pageSize, Sort.Direction direction, String sortBy) {
        return PageRequest.of(pageNumber - 1, pageSize, direction, sortBy);
    }

}
