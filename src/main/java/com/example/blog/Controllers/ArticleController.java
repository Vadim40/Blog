package com.example.blog.Controllers;

import com.example.blog.Mappers.ArticleMapper;
import com.example.blog.Models.Article;
import com.example.blog.Models.DTOs.ArticleDTO;
import com.example.blog.Models.DTOs.ArticleViewDTO;
import com.example.blog.Models.DTOs.ValidationErrorResponse;
import com.example.blog.Services.Implementations.ArticleServiceImpl;
import com.example.blog.Services.Implementations.TopicServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleServiceImpl articleService;
    private final TopicServiceImpl topicService;
    private final ArticleMapper articleMapper;

    @GetMapping("/topic/{topicName}")
    public ResponseEntity<Object> findArticlesByTopic(@PathVariable String topicName,
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
            log.warn("No articles found by this topic: {}", topicName);
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToDTOAndSetStates);
        CustomApiResponse customApiResponse = new CustomApiResponse(articleViewDTOPage,
                topicService.findTopicFollowersCount(topicName));
        return new ResponseEntity<>(customApiResponse, HttpStatus.OK);
    }


    @GetMapping("/user/{username}")
    public ResponseEntity<Page<ArticleViewDTO>> findArticlesByUser(@PathVariable String username,
                                                                   @RequestParam(defaultValue = "20") int pageSize,
                                                                   @RequestParam(defaultValue = "1") int pageNumber,
                                                                   @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                   @RequestParam(defaultValue = "creationDate") String sortBy) {

        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findPublishedArticlesByUserUsername(username, pageable);
        if (articles.isEmpty()) {
            log.warn("No articles found by this user: {}", username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToDTOAndSetStates);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }

    @GetMapping("/favorites")
    public ResponseEntity<Page<ArticleViewDTO>> findFavoriteArticles(@RequestParam(defaultValue = "20") int pageSize,
                                                                     @RequestParam(defaultValue = "1") int pageNumber,
                                                                     @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                     @RequestParam(defaultValue = "creationDate") String sortBy) {
        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findPublishedFavoriteArticlesByAuthenticationUser(pageable);
        if (articles.isEmpty()) {
            log.warn("No favorites found ");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToDTOAndSetStates);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }

    @GetMapping("/drafts")
    public ResponseEntity<Page<ArticleViewDTO>> findDraftArticles(@RequestParam(defaultValue = "20") int pageSize,
                                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                                  @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                  @RequestParam(defaultValue = "creationDate") String sortBy) {
        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findNotPublishedArticlesByAuthenticationUser(pageable);
        if (articles.isEmpty()) {
            log.warn("No drafts found ");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToDTOAndSetStates);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }


    @GetMapping("/interests")
    public ResponseEntity<Page<ArticleViewDTO>> findArticlesByUserTopicOfInterest(@RequestParam(defaultValue = "20") int pageSize,
                                                                                  @RequestParam(defaultValue = "1") int pageNumber,
                                                                                  @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                  @RequestParam(defaultValue = "creationDate") String sortBy) {
        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findPublishedArticlesByUserTopicsOfInterest(pageable);
        if (articles.isEmpty()) {
            log.warn("No interests found ");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToDTOAndSetStates);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<ArticleViewDTO>> findArticlesByTitleIsContainingIgnoreCase(@RequestParam String title,
                                                                                          @RequestParam(defaultValue = "20") int pageSize,
                                                                                          @RequestParam(defaultValue = "1") int pageNumber,
                                                                                          @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                          @RequestParam(defaultValue = "creationDate") String sortBy) {
        Pageable pageable = createPageable(pageNumber, pageSize, direction, sortBy);
        Page<Article> articles = articleService.findPublishedArticlesByTitleIsContainingIgnoreCaseString(title, pageable);
        if (articles.isEmpty()) {
            log.warn("No articles found by this title: {}", title);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Page<ArticleViewDTO> articleViewDTOPage = articles.map(this::mapArticleToDTOAndSetStates);
        return new ResponseEntity<>(articleViewDTOPage, HttpStatus.OK);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<Object> findArticleById(@PathVariable long articleId) {
        Article article = articleService.findPublishedArticleById(articleId);
        ArticleViewDTO articleViewDTO = mapArticleToDTOAndSetStates(article);
        return new ResponseEntity<>(articleViewDTO, HttpStatus.OK);
    }


    @PutMapping("/{articleId}/publish")
    public ResponseEntity<Object> publishArticle(@PathVariable long articleId) {
        articleService.publishArticle(articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createArticle(@RequestBody @Valid ArticleDTO articleDTO,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ValidationErrorResponse errorResponse=new ValidationErrorResponse(bindingResult);
            log.warn("validation errors creating Article: {}",errorResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        Article article = articleMapper.mapToArticle(articleDTO);
        article = articleService.saveArticle(article);
        ArticleDTO savedArticleDTO = articleMapper.mapToArticleDTO(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticleDTO);
    }

    @PutMapping("/{articleId}/update")
    public ResponseEntity<Object> updateArticle(
            @PathVariable long articleId,
            @RequestBody @Valid ArticleDTO articleDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ValidationErrorResponse errorResponse=new ValidationErrorResponse(bindingResult);
            log.warn("validation errors updating Article: {}", errorResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        Article article = articleMapper.mapToArticle(articleDTO);
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

    private ArticleViewDTO mapArticleToDTOAndSetStates(Article article) {
        boolean isArticleFavorite = articleService.isArticleFavorite(article.getId());
        boolean isArticleLiked = articleService.isArticleLiked(article.getId());
        return articleMapper.mapToArticleViewDTO(article, isArticleFavorite, isArticleLiked);
    }


    private Pageable createPageable(int pageNumber, int pageSize, Sort.Direction direction, String sortBy) {
        return PageRequest.of(pageNumber - 1, pageSize, direction, sortBy);
    }

}
