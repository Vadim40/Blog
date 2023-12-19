package com.example.blog.Excteptions;

public class TopicAlreadyExistsException extends RuntimeException{
    public TopicAlreadyExistsException(String message){
        super(message);
    }
}
