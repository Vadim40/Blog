package com.example.blog.Repositories;

import com.example.blog.Models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findImagesByArticleId(long articleId);

    Optional<Image> findImageByUserId(long userId);
}
