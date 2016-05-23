package com.projectsandbox.components.website.validation.annotation;

import com.projectsandbox.components.website.validation.validator.MultipartMimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by limpygnome on 12/08/15.
 */
@Documented
@Constraint(validatedBy = MultipartMimeValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartMimeType
{
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowedMimeTypes();
}
