package com.example.blog.Mappers;

import com.example.blog.Models.DTOs.ImageDTO;
import com.example.blog.Models.Image;

public class ImageMapper {
    public ImageDTO toDTO(Image image){
        return ImageDTO.builder()
                .id(image.getId())
                .name(image.getName())
                .imageData(image.getImageData())
                .build();
    }
    public Image toEntity(ImageDTO imageDTO){
        return Image.builder()
                .id(imageDTO.getId())
                .name(imageDTO.getName())
                .imageData(imageDTO.getImageData())
                .build();
    }
}
