package com.limpygnome.projectsandbox.website.validation.validator;

import com.limpygnome.projectsandbox.website.validation.annotation.SizeWhenSpecified;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by limpygnome on 23/07/15.
 */
public class SizeWhenSpecifiedValidator implements ConstraintValidator<SizeWhenSpecified, String>
{
    private SizeWhenSpecified instance;

    @Override
    public void initialize(SizeWhenSpecified instance)
    {
        this.instance = instance;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        if (value == null)
        {
            return true;
        }

        int length = value.length();

        return length == 0 || ( length >= instance.min() && length <= instance.max() );
    }
}
