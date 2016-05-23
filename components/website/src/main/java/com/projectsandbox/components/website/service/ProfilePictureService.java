package com.projectsandbox.components.website.service;

import com.projectsandbox.components.shared.model.User;
import com.projectsandbox.components.website.model.result.ProfilePictureProcessFileResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by limpygnome on 10/08/15.
 */
public interface ProfilePictureService
{

    ProfilePictureProcessFileResult processUploadedFile(User user, MultipartFile multipartFile);

}
