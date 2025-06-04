package com.project.spring_project.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotEmailValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmail {
    String message() default "{username.must.not.be.email}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}