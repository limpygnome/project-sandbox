package com.limpygnome.projectsandbox.website.service;

import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.model.result.ProfilePictureProcessFileResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by limpygnome on 10/08/15.
 */
public interface ProfilePictureService
{

    ProfilePictureProcessFileResult processUploadedFile(User user, MultipartFile multipartFile);

}
