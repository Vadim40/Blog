package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    Image saveImage(Image image);

    Image findImageById(long imageId);

    List<Image> findImagesByArticleId(long articleId);

    Image findAvatarByUserId(long userId);

    Image updateImage(Image image, long imageId);

    void deleteImage(long imageId);
}
