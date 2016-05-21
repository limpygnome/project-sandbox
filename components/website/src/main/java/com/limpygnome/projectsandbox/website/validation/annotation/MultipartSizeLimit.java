package com.limpygnome.projectsandbox.website.validation.annotation;

import com.limpygnome.projectsandbox.website.validation.validator.MultipartSizeLimitValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by limpygnome on 12/08/15.
 */
@Documented
@Constraint(validatedBy = MultipartSizeLimitValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartSizeLimit
{
    long bytesLimit();

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
