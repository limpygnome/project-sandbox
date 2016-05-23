package com.projectsandbox.components.website.validation.annotation;

import com.projectsandbox.components.website.validation.validator.SizeWhenSpecifiedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by limpygnome on 23/07/15.
 */
@Documented
@Constraint(validatedBy = SizeWhenSpecifiedValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SizeWhenSpecified
{
    String message() default "{password.length}";

    int min() default 1;

    int max() default 255;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
