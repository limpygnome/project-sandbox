package com.limpygnome.projectsandbox.website.validation.validator;

import com.limpygnome.projectsandbox.website.validation.annotation.NicknameChars;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created by limpygnome on 23/07/15.
 */
public class NicknameCharsValidator implements ConstraintValidator<NicknameChars, String>
{
    private static final Pattern NICKNAME_REGEX_PATTERN = Pattern.compile("^(?!guest\\_)([a-zA-Z0-9\\_\\-\\$]+)$");
    private static final Pattern UUID_REGEX_PATTERN = Pattern.compile("^([a-fA-F0-9]{8})\\-(([a-fA-F0-9]{4})\\-){3}([a-fA-F0-9]{12})$");

    @Override
    public void initialize(NicknameChars username) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        // Check chars and does not start with `guest_`
        if (value == null || !NICKNAME_REGEX_PATTERN.matcher(value).matches())
        {
            return false;
        }
        // Check does not match UUID, since this will mess with profile system
        else if (UUID_REGEX_PATTERN.matcher(value).matches())
        {
            return false;
        }

        return true;
    }
}
