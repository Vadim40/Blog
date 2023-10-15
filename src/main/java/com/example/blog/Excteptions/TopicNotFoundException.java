package com.example.blog.Excteptions;

public class TopicNotFoundException extends RuntimeException{
    public TopicNotFoundException(String message){
        super(message);
    }
}
