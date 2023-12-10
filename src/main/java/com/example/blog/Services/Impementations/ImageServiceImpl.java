package com.example.blog.Services.Impementations;

import com.example.blog.Excteptions.ImageNotFoundException;
import com.example.blog.Models.Image;
import com.example.blog.Repositories.ImageRepository;
import com.example.blog.Services.Interfaces.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
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
    public List<Image> findImagesByArticleId(long articleId) {
        return imageRepository.findImagesByArticleId(articleId);
    }

    @Override
    public Image findAvatarByUserId(long userId) {
        return imageRepository.findImageByUserId(userId).orElseThrow(() -> new ImageNotFoundException("Image not found"));
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
