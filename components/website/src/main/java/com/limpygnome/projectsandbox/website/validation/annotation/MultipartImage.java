package com.limpygnome.projectsandbox.website.validation.annotation;

import com.limpygnome.projectsandbox.website.validation.validator.MultipartImageValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by limpygnome on 12/08/15.
 */
@Documented
@Constraint(validatedBy = MultipartImageValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartImage
{
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
