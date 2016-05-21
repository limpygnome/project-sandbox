package com.limpygnome.projectsandbox.website.validation.validator;

import com.limpygnome.projectsandbox.website.validation.annotation.Email;
import com.limpygnome.projectsandbox.website.validation.annotation.NicknameChars;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created by limpygnome on 23/07/15.
 */
public class EmailValidator implements ConstraintValidator<Email, String>
{
    private static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile("^([a-zA-Z0-9\\-\\_\\.\\+]+)@([a-zA-Z0-9\\-]+)((\\.[a-z]{2,3}){1,4})$");

    @Override
    public void initialize(Email email) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        // Check chars
        if (value == null || !EMAIL_REGEX_PATTERN.matcher(value).matches())
        {
            return false;
        }

        return true;
    }
}
