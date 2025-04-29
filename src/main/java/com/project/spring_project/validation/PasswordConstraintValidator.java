package com.project.spring_project.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) return false;

        return password.length() >= 8
                && password.length() <= 50
                && password.matches(".*[A-Z].*") // at least one uppercase
                && password.matches(".*[a-z].*") // at least one lowercase
                && password.matches(".*\\d.*")   // at least one number
                && password.matches(".*[@#$%^&+=!?].*"); // at least one special character
    }
}