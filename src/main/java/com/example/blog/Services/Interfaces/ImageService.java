package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    Image saveImage(MultipartFile file);
    Image findImageById(long imageId);
    List<Image> findImagesByArticleId(long articleId);
    Image findAvatarByUserId(long userId);
    Image updateImage(MultipartFile file, long imageId);
    void deleteImage(long imageId);
}
