package com.project.spring_project.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Documented;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    /**
     * Initializes the validator. This method is called before the validation process starts.
     *
     * @param password The password to be validated.
     * @param context The context in which the constraint is evaluated.
     */
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