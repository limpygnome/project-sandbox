package com.projectsandbox.components.website.validation.annotation;

import com.projectsandbox.components.website.validation.validator.EmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by limpygnome on 23/07/15.
 */
@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Email
{
    String message() default "{email.format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
