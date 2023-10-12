package com.example.blog.Services;

import com.example.blog.Excteptions.ImageNotFoundException;
import com.example.blog.Models.Article;
import com.example.blog.Models.Image;
import com.example.blog.Models.User;
import com.example.blog.Repositories.ArticleRepository;
import com.example.blog.Repositories.ImageRepository;
import com.example.blog.Services.Interfaces.IImageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;

    @Override
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public Image findImageById(long imageId) {
        return imageRepository.findById(imageId).orElseThrow(
                () -> new ImageNotFoundException("image not found."));
    }

    @Override
    public Image updateImage(Image image, long imageId) {
        image.setId(imageId);
        return imageRepository.save(image);
    }

    @Override
    public void deleteImage(long imageId) {
        imageRepository.deleteById(imageId);
    }
}
