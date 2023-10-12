package com.example.blog.Excteptions;

public class ImageNotFoundException extends RuntimeException{
    public ImageNotFoundException(String message){
        super(message);
    }
}
