package com.example.blog.Services;

import com.example.blog.Excteptions.ImageNotFoundException;
import com.example.blog.Models.Image;
import com.example.blog.Repositories.ImageRepository;
import com.example.blog.Services.Interfaces.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

    @Override
    public Image saveImage(MultipartFile file)
    {
        try {
            return imageRepository.save(Image.builder()
                    .name(file.getOriginalFilename())
                    .imageData(file.getBytes())
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return imageRepository.findImageByUserId(userId).orElseThrow(()->new ImageNotFoundException("Image not found"));
    }

    @Override
    public Image updateImage(MultipartFile file, long imageId) {

        try {
          Image image = Image.builder()
                     .name(file.getOriginalFilename())
                     .imageData(file.getBytes())
                     .build();
            image.setId(imageId);
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteImage(long imageId) {
        imageRepository.deleteById(imageId);
    }


}
