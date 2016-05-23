package com.projectsandbox.components.website.service.imp;

import com.projectsandbox.components.shared.model.User;
import com.projectsandbox.components.website.model.result.ProfilePictureProcessFileResult;
import com.projectsandbox.components.website.service.ProfilePictureService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by limpygnome on 10/08/15.
 */
@Service
public class DefaultProfilePictureService implements ProfilePictureService
{
    private final static Logger LOG = LogManager.getLogger(DefaultProfilePictureService.class);

    @Value("${path.profile.pictures}")
    private String pathProfilePictures;

    @Bean
    public String validatePathProfilePictures()
    {
        if (pathProfilePictures == null || pathProfilePictures.length() == 0 || pathProfilePictures.startsWith("{"))
        {
            throw new IllegalArgumentException("Invalid value for path profile pictures - '" + pathProfilePictures + "'");
        }

        // Trim tailing slash
        if (pathProfilePictures.length() != 1 && pathProfilePictures.endsWith("/"))
        {
            pathProfilePictures = pathProfilePictures.substring(0, pathProfilePictures.length() - 1);
        }

        // Check path exists, else create it
        File filePathProfilePictures = new File(pathProfilePictures);

        if (!filePathProfilePictures.exists())
        {
            filePathProfilePictures.mkdirs();

            LOG.info("Path - created - profile pictures: {}", pathProfilePictures);
        }
        else
        {
            LOG.info("Path - exists - profile pictures: {}", pathProfilePictures);
        }

        return pathProfilePictures;
    }

    @Override
    public ProfilePictureProcessFileResult processUploadedFile(User user, MultipartFile multipartFile)
    {
        // Build destination path
        String userId = user.getUserId();

        if (userId == null)
        {
            throw new RuntimeException("User ID cannot be null");
        }

        String destinationUpload = pathProfilePictures + "/" + userId + ".png";

        try
        {
            // Write upload to file
            InputStream inputStream = multipartFile.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(destinationUpload, false);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.flush();
            fileOutputStream.close();

            LOG.debug("Uploaded profile picture - path: {}", destinationUpload);

            return ProfilePictureProcessFileResult.SUCCESS;
        }
        catch (Exception e)
        {
            LOG.error("Failed to write profile picture to disk - path: {}", destinationUpload, e);

            return ProfilePictureProcessFileResult.FAILURE;
        }
    }

}
