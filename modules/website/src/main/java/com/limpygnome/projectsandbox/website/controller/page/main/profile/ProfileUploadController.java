package com.limpygnome.projectsandbox.website.controller.page.main.profile;

import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.model.form.profile.ProfilePictureUploadForm;
import com.limpygnome.projectsandbox.website.model.result.ProfilePictureProcessFileResult;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import com.limpygnome.projectsandbox.website.service.ProfilePictureService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Created by limpygnome on 10/08/15.
 */
@Controller
public class ProfileUploadController extends BaseController
{
    private final static Logger LOG = LogManager.getLogger(ProfileUploadController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProfilePictureService profilePictureService;

    @ModelAttribute("profilePictureUploadForm")
    public ProfilePictureUploadForm profilePictureUploadForm()
    {
        return new ProfilePictureUploadForm();
    }

    @RequestMapping(value = "/profile/upload", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView profileUploadProcess(
            @ModelAttribute("profilePictureUploadForm") @Valid ProfilePictureUploadForm profilePictureUploadForm,
            BindingResult bindingResult, HttpSession httpSession)
    {
        User user = authenticationService.retrieveCurrentUser(httpSession);

        // Check user is authorised
        if (user == null)
        {
            return createMvPage404();
        }

        // Create MV ready for result, in case of upload
        ModelAndView modelAndView = createMV("main/profile_upload", "profile - upload");;

        if (profilePictureUploadForm != null && !bindingResult.hasErrors())
        {
            // Handle uploaded file
            ProfilePictureProcessFileResult result = profilePictureService.processUploadedFile(
                    user,profilePictureUploadForm.getFileUpload()
            );

            // Attach result
            switch (result)
            {
                case FAILURE:
                    break;
                case SUCCESS:
                    break;
            }
        }

        return modelAndView;
    }

}
