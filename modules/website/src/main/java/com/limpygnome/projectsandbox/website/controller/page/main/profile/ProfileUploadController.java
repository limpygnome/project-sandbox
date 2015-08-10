package com.limpygnome.projectsandbox.website.controller.page.main.profile;

import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.model.form.profile.ProfilePictureUploadForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
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

/**
 * Created by limpygnome on 10/08/15.
 */
@Controller
public class ProfileUploadController extends BaseController
{
    private final static Logger LOG = LogManager.getLogger(ProfileUploadController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/profile/upload", method = RequestMethod.GET)
    public ModelAndView profileUpload(HttpSession httpSession)
    {
        User user = authenticationService.retrieveCurrentUser(httpSession);

        // Check user is authorised
        if (user == null)
        {
            return createMvPage404();
        }

        // Build page
        ModelAndView modelAndView = createMV("main/profile_upload", "profile - upload");

        return modelAndView;
    }

    @RequestMapping(value = "/profile/upload", method = RequestMethod.POST)
    public ModelAndView profileUploadProcess(
            @ModelAttribute("profilePictureUploadForm") ProfilePictureUploadForm profilePictureUploadForm,
            BindingResult bindingResult, HttpSession httpSession)
    {
        User user = authenticationService.retrieveCurrentUser(httpSession);

        // Check user is authorised
        if (user == null)
        {
            return createMvPage404();
        }

        // Handle uploaded file

        // Render normal layout
        return profileUpload(httpSession);
    }

    @ModelAttribute("profilePictureUploadForm")
    public ProfilePictureUploadForm profilePictureUploadForm()
    {
        return new ProfilePictureUploadForm();
    }

}
