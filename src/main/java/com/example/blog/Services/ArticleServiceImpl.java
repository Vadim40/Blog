package com.example.blog.Services;

import com.example.blog.Excteptions.ArticleNotFoundException;
import com.example.blog.Models.Article;
import com.example.blog.Models.Enums.Role;
import com.example.blog.Models.Image;
import com.example.blog.Models.Topic;
import com.example.blog.Models.User;
import com.example.blog.Repositories.ArticleRepository;
import com.example.blog.Repositories.UserRepository;
import com.example.blog.Services.Interfaces.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    @Override
    public Page<Article> findArticlesByUserId(long userId, int size, int page) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findArticlesByUserId(userId, pageable);
    }

    @Override
    public Set<Article> findSavedArticlesByAuthenticationUser() {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        return authenticationUser.getSavedArticles();
    }

    @Override
    public Page<Article> findArticlesByTopic(long topicId, int size, int page) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findArticlesByTopicsId(topicId, pageable);
    }

    @Override
    public Page<Article> findArticlesByUserTopicOfInterest(int size, int page) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Set<Article> articles = new HashSet<>();
        for (Topic topic : authenticatedUser.getTopicsOfInterest()) {
            articles.addAll(findArticlesByTopic(topic.getId(), size, page).getContent());
        }
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(List.copyOf(articles), pageable, articles.size());
    }


    @Override
    public Page<Article> findArticlesByTitleIsContainingIgnoreCaseString(String title, int size, int page) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findArticlesByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    public Page<Article> findArticlesBySorting(int size, int page, Sort.Direction direction, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        return articleRepository.findAll(pageable);
    }

    @Override
    public Article findArticleByCommentId(long commentId) {
        return articleRepository.findArticleByCommentsId(commentId);
    }

    @Override
    public Article findArticleById(long articleId) {
        return articleRepository.findById(articleId).orElseThrow(() -> new ArticleNotFoundException("Article not found."));
    }

    @Override
    public Article publishArticle(Article article) {
        article.setPublished(true);
        return articleRepository.save(article);
    }

    @Override
    public void addToSavedUserArticles(long articleId) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        Article article = findArticleById(articleId);
        if (authenticationUser.getSavedArticles().contains(article)) {
            throw new IllegalArgumentException("You are already saved this user.");
        }
        authenticationUser.getSavedArticles().add(article);
        userRepository.save(authenticationUser);
    }

    @Override
    public void removeFromSavedUserArticles(long articleId) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        Article article = findArticleById(articleId);
        if (!authenticationUser.getSavedArticles().contains(article)) {
            throw new IllegalArgumentException("You are not saved this article you are trying to remove.");
        }
        authenticationUser.getSavedArticles().remove(article);
        userRepository.save(authenticationUser);
    }

    @Override
    public Article saveArticle(Article article) {
        User authenticationUser = customUserDetailsService.getAuthenticatedUser();
        article.setUser(authenticationUser);
        article.setCreationDate(LocalDate.now());
        return articleRepository.save(article);
    }
    @Override
    public void saveImageToArticle(Image image, long articleId) {
        Article article = findArticleById(articleId);
        String text =article.getText().concat(" [image:"+article.getImages().size()+1+"] ");
        article.setText(text);
        article.getImages().add(image);
        articleRepository.save(article);
    }

    @Override
    public Article updateArticleById(Article article, long articleId) {
        checkArticleAccess(articleId);
        if (findArticleById(articleId).isPublished()) {
            throw new UnsupportedOperationException("You dont have permission to update this article");
        }
        article.setId(articleId);
        return articleRepository.save(article);
    }

    @Override
    public void deleteArticleById(long articleId) {
        checkArticleAccess(articleId);
        articleRepository.deleteById(articleId);
    }

    private void checkArticleAccess(long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Article articleToCheck = findArticleById(articleId);
        if (!authenticatedUser.getRoles().contains(Role.ADMIN) && !authenticatedUser.getArticles().contains(articleToCheck)) {
            throw new AccessDeniedException("You don't have permission to perform this action on this article");
        }
    }

    @Override
    @Transactional
    public void putLike(long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Article article = findArticleById(articleId);
        if (authenticatedUser.getLikedArticles().contains(articleId)) {
            throw new IllegalArgumentException("You have already  liked this article");
        }
        article.setLikes(article.getLikes() + 1);
        authenticatedUser.getLikedArticles().add(articleId);
        userRepository.save(authenticatedUser);
        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void removeLike(long articleId) {
        User authenticatedUser = customUserDetailsService.getAuthenticatedUser();
        Article article = findArticleById(articleId);
        if (!authenticatedUser.getLikedArticles().contains(articleId)) {
            throw new IllegalArgumentException("You have not liked this article");
        }
        article.setLikes(article.getLikes() - 1);
        authenticatedUser.getLikedArticles().remove(articleId);
        userRepository.save(authenticatedUser);
        articleRepository.save(article);
    }
}
