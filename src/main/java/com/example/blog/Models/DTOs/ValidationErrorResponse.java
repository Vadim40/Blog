package com.example.blog.Models.DTOs;

import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

@Data
public class ValidationErrorResponse {
    private Map<String, String> errors;
    public ValidationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = getErrorFieldName(error);
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        this.errors = errors;
    }

    private String getErrorFieldName(ObjectError error) {
        if (error instanceof FieldError) {
            return ((FieldError) error).getField();
        } else {
            return error.getObjectName();
        }
    }
}
