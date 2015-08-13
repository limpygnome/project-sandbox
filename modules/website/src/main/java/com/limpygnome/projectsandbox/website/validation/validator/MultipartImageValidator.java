package com.limpygnome.projectsandbox.website.validation.validator;

import com.limpygnome.projectsandbox.website.validation.annotation.MultipartImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.InputStream;

/**
 * Created by limpygnome on 12/08/15.
 */
public class MultipartImageValidator implements ConstraintValidator<MultipartImage, MultipartFile>
{
    private final static Logger LOG = LogManager.getLogger(MultipartImageValidator.class);

    private MultipartImage multipartImageDimensions;

    @Override
    public void initialize(MultipartImage multipartImageDimensions)
    {
        this.multipartImageDimensions = multipartImageDimensions;
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext)
    {
        if (multipartFile == null)
        {
            return true;
        }

        try
        {
            // Read image, to check it can be read, and validate dimensions
            InputStream inputStream = multipartFile.getInputStream();
            ImageIO.read(inputStream);
            inputStream.reset();

            return true;
        }
        catch (Exception e)
        {
            LOG.debug("Invalid file uploaded, cannot be read as image", e);

            return false;
        }
    }

}
