package com.limpygnome.projectsandbox.website.validation.annotation;

import com.limpygnome.projectsandbox.website.validation.validator.NicknameCharsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by limpygnome on 23/07/15.
 */
@Documented
@Constraint(validatedBy = NicknameCharsValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NicknameChars
{
    String message() default "{nickname.chars}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
