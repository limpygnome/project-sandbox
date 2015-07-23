package com.limpygnome.projectsandbox.website.validation.validator;

import com.limpygnome.projectsandbox.website.validation.annotation.Username;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by limpygnome on 23/07/15.
 */
public class UsernameValidator implements ConstraintValidator<Username, String>
{
    @Override
    public void initialize(Username username) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        if (value == null || value.length() == 0)
        {
            return false;
        }
        return value.matches("^([a-zA-Z0-9\\_\\-\\$]+)$");
    }
}
