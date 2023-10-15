package com.example.blog.Excteptions;

public class TopicAlreadyExists extends RuntimeException{
    public TopicAlreadyExists(String message){
        super(message);
    }
}
