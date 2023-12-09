package com.example.blog.Controllers;

import com.example.blog.Mappers.ArticleMapper;
import com.example.blog.Models.Article;
import com.example.blog.Models.DTOs.ArticleDTO;
import com.example.blog.Services.ArticleServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleServiceImpl articleService;
    private final ArticleMapper articleMapper;


    @GetMapping("/topic/{topicId}")
    public String findArticlesByTopic(
            @PathVariable long topicId,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model) {
        Page<Article> articles = articleService.findArticlesByTopic(topicId, pageSize, pageNumber);
        Page<ArticleDTO> articleDTOS = articles.map(articleMapper::mapToDTO);
        model.addAttribute("articles", articleDTOS);
        return "articlesByTopic";
    }

    @GetMapping("/{username}")
    public String findArticlesByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model) {
        Page<Article> articles = articleService.findArticlesByUser_Username(username, pageSize, pageNumber);
        Page<ArticleDTO> articleDTOS = articles.map(articleMapper::mapToDTO);
        model.addAttribute("articles", articleDTOS);
        return "articleByUser";
    }


    @GetMapping("/interests")
    public String findArticlesByUserTopicOfInterest(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model) {
        Page<Article> articles = articleService.findArticlesByUserTopicOfInterest(pageSize, pageNumber);
        Page<ArticleDTO> articleDTOS = articles.map(articleMapper::mapToDTO);
        model.addAttribute("articles", articleDTOS);
        return "articleByInterests";
    }

    @GetMapping("/sorted")
    public String findArticlesBySorting(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            Model model) {

        Page<Article> articles = articleService.findArticlesBySorting(pageSize, pageNumber, direction, sortBy);
        Page<ArticleDTO> articleDTOs = articles.map(articleMapper::mapToDTO);
        model.addAttribute("articles", articleDTOs);

        return "articlesSorted";
    }

    @GetMapping()
    public String findArticlesByTitleIsContainingIgnoreCase(
            @RequestParam String title,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model) {
        Page<Article> articles = articleService.findArticlesByTitleIsContainingIgnoreCaseString(title, pageSize, pageNumber);
        Page<ArticleDTO> articleDTOS = articles.map(articleMapper::mapToDTO);
        model.addAttribute("articles", articleDTOS);
        return "articleByInterests";
    }

    @GetMapping("/{articleId}")
    public String findArticleById(
            @PathVariable long articleId,
            Model model) {
        Article article = articleService.findArticleById(articleId);
        ArticleDTO articleDTO = articleMapper.mapToDTO(article);
        model.addAttribute("article", articleDTO);
        model.addAttribute("articleIsLiked", articleService.isArticleLiked(articleId));
        model.addAttribute("articleIsFavorite", articleService.isArticleFavorite(articleId));
        return "show";
    }

    @PutMapping("/{articleId}/publish")
    public String publishArticle(
            @PathVariable long articleId) {
        articleService.publishArticle(articleId);
        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/save")
    public String saveArticle(
            @ModelAttribute @Valid ArticleDTO articleDTO, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "some";
        }
        Article article = articleMapper.mapToEntity(articleDTO);
        article = articleService.saveArticle(article);
        return "redirect:/articles/" + article.getId();
    }

    @PutMapping("/{articleId}/update")
    public String updateArticle(
            @PathVariable long articleId,
            @ModelAttribute @Valid ArticleDTO articleDTO,
            BindingResult bindingResult,
            Model model) {
        if(bindingResult.hasErrors()){
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "some";
        }
        Article article = articleMapper.mapToEntity(articleDTO);
        articleService.updateArticleById(article, articleId);
        return "redirect:/articles/" + articleId;
    }



    @PutMapping("/{articleId}/toggle-favorite")
    public String toggleFavoriteStatus(
            @PathVariable long articleId) {
        articleService.toggleFavoriteStatus(articleId);
        return "redirect:/articles/" + articleId;
    }

    @PutMapping("/{articleId}/toggle-like")
    public String toggleLikeStatus(
            @PathVariable long articleId) {
        articleService.toggleLikeStatus(articleId);
        return "redirect:/articles/" + articleId;
    }

    @DeleteMapping("/{articleId}/delete")
    public String deleteArticle(
            @PathVariable long articleId) {
        articleService.deleteArticleById(articleId);
        return "delete";
    }

}
