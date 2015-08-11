package com.limpygnome.projectsandbox.website.model.form.profile;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * Created by limpygnome on 10/08/15.
 */
public class ProfilePictureUploadForm
{
    @NotNull(message = "{profile.picture.upload_invalid}")
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
