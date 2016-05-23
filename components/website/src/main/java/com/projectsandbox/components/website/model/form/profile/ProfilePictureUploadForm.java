package com.projectsandbox.components.website.model.form.profile;

import com.projectsandbox.components.website.validation.annotation.MultipartImage;
import com.projectsandbox.components.website.validation.annotation.MultipartMimeType;
import com.projectsandbox.components.website.validation.annotation.MultipartSizeLimit;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by limpygnome on 10/08/15.
 */
public class ProfilePictureUploadForm
{
    @MultipartMimeType(allowedMimeTypes = { "image/png", "image/gif", "image/jpg", "image/jpeg" }, message = "{profile.picture.upload_invalid}" )
    @MultipartSizeLimit(bytesLimit = 819200, message = "{profile.picture.upload_size}")
    @MultipartImage(message = "{profile.picture.upload_invalid}")
    private MultipartFile fileUpload;

    public ProfilePictureUploadForm()
    {
        this.fileUpload = null;
    }

    public MultipartFile getFileUpload()
    {
        return fileUpload;
    }

    public void setFileUpload(MultipartFile fileUpload)
    {
        this.fileUpload = fileUpload;
    }

}
