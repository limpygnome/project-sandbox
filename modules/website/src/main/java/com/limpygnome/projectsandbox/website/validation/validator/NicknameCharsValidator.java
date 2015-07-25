package com.limpygnome.projectsandbox.website.validation.validator;

import com.limpygnome.projectsandbox.website.validation.annotation.NicknameChars;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by limpygnome on 23/07/15.
 */
public class NicknameCharsValidator implements ConstraintValidator<NicknameChars, String>
{
    @Override
    public void initialize(NicknameChars username) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        // Check chars
        if (value != null && !value.matches("^([a-zA-Z0-9\\_\\-\\$]+)$"))
        {
            return false;
        }

        return true;
    }
}
