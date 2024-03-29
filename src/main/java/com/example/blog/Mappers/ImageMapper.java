package com.example.blog.Mappers;

import com.example.blog.Models.DTOs.ImageDTO;
import com.example.blog.Models.Image;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ImageMapper {
    public ImageDTO mapToImageDTO(Image image) {
        if (image == null) {
            return null;
        }
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setId(image.getId());
        imageDTO.setName(image.getName());
        imageDTO.setImageData(image.getImageData());
        return imageDTO;
    }

    public Image mapToImage(ImageDTO imageDTO) {
        Image image = new Image();
        image.setId(imageDTO.getId());
        image.setName(imageDTO.getName());
        image.setImageData(imageDTO.getImageData());
        return image;
    }

    public Image mapToImageFromMultipartFile(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setImageData(file.getBytes());
        return image;
    }
}
