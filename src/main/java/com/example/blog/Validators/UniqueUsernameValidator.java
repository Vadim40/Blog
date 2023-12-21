package com.example.blog.Validators;

import com.example.blog.Services.Implementations.UserServiceImpl;
import com.example.blog.Validators.Annotations.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private  final UserServiceImpl userService;



    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return isUsernameUnique(name);
    }

    private boolean isUsernameUnique(String username) {
        return !userService.IsUsernameExists(username);
    }
}
