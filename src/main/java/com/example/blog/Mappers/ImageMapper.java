package com.example.blog.Mappers;

import com.example.blog.Models.DTOs.ImageDTO;
import com.example.blog.Models.Image;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ImageMapper {
    public ImageDTO mapToDTO(Image image){
        return ImageDTO.builder()
                .id(image.getId())
                .name(image.getName())
                .imageData(image.getImageData())
                .build();
    }
    public Image mapToEntity(ImageDTO imageDTO){
        return Image.builder()
                .id(imageDTO.getId())
                .name(imageDTO.getName())
                .imageData(imageDTO.getImageData())
                .build();
    }
    public Image mapToEntityFromMultipartFile(MultipartFile file) throws IOException {
     return Image.builder().name(file.getOriginalFilename())
                .imageData(file.getBytes())
                .build();
    }
}
