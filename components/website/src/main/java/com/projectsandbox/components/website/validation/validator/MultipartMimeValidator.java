package com.projectsandbox.components.website.validation.validator;

import com.projectsandbox.components.website.validation.annotation.MultipartMimeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.InputStream;

/**
 * Created by limpygnome on 12/08/15.
 */
public class MultipartMimeValidator implements ConstraintValidator<MultipartMimeType, MultipartFile>
{
    private final static Logger LOG = LogManager.getLogger(MultipartMimeValidator.class);

    private MultipartMimeType multipartMimeType;

    @Override
    public void initialize(MultipartMimeType multipartMimeType)
    {
        this.multipartMimeType = multipartMimeType;
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
            InputStream inputStream = multipartFile.getInputStream();

            // Detect content type
            Tika tika = new Tika();
            String detectedMediaType = tika.detect(inputStream);

            // Reset stream
            inputStream.reset();

            // Check content type is allowed
            for (String mediaType : multipartMimeType.allowedMimeTypes())
            {
                if (mediaType.equals(detectedMediaType))
                {
                    return true;
                }
            }

            LOG.debug("Invalid uploaded MIME type - type: {}", detectedMediaType.toString());

            return false;
        }
        catch (Exception e)
        {
            LOG.error("Failed to validate MIME type of upload", e);

            return false;
        }
    }

}
