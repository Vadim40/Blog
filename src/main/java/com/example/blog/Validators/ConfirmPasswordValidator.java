package com.example.blog.Validators;

import com.example.blog.Models.DTOs.RegistrationUserDTO;
import com.example.blog.Validators.Annotations.ConfirmPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ConfirmPasswordValidator implements ConstraintValidator<ConfirmPassword, Object> {


    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        RegistrationUserDTO registrationUserDTO=(RegistrationUserDTO) object;
        return registrationUserDTO.getPassword().equals(registrationUserDTO.getConfirmPassword());
    }
}
