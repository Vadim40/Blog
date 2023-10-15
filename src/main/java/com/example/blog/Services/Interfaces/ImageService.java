package com.example.blog.Services.Interfaces;

import com.example.blog.Models.Image;

public interface ImageService {

    Image saveImage(Image image);
    Image findImageById(long imageId);
    Image updateImage(Image image, long imageId);
    void deleteImage(long imageId);
}
