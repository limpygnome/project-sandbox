package com.projectsandbox.components.website.validation.validator;

import com.projectsandbox.components.website.validation.annotation.MultipartSizeLimit;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by limpygnome on 12/08/15.
 */
public class MultipartSizeLimitValidator implements ConstraintValidator<MultipartSizeLimit, MultipartFile>
{
    private MultipartSizeLimit multipartSizeLimit;

    @Override
    public void initialize(MultipartSizeLimit multipartSizeLimit)
    {
        this.multipartSizeLimit = multipartSizeLimit;
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext)
    {
        return multipartFile == null || multipartFile.getSize() <= multipartSizeLimit.bytesLimit();
    }

}
