package com.example.blog.Validators;

import com.example.blog.Services.Implementations.TopicServiceImpl;
import com.example.blog.Validators.Annotations.UniqueName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {
    private  final TopicServiceImpl topicService;



    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return isNameUnique(name);
    }

    private boolean isNameUnique(String name) {
        return !topicService.isTopicExists(name);
    }
}
