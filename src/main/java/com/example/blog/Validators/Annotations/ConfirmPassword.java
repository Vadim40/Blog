package com.example.blog.Validators.Annotations;

import com.example.blog.Validators.ConfirmPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ConfirmPasswordValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfirmPassword {
    String message() default "Passwords doesn't match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
