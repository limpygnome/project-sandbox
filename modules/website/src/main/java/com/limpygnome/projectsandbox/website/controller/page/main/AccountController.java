package com.limpygnome.projectsandbox.website.controller.page.main;

import com.limpygnome.projectsandbox.shared.model.Password;
import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.model.form.account.UpdateDetailsForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Used by the user for changing their account.
 */
@Controller
@RequestMapping("/account")
public class AccountController extends BaseController
{
    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView accountView()
    {
        return createMV("main/account", "account");
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ModelAndView accountProcessUpdate(@ModelAttribute("updateDetailsForm") @Valid  UpdateDetailsForm updateDetailsForm,
                                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                             HttpSession httpSession)
    {
        // Fetch current user
        User user = authenticationService.retrieveCurrentUser(httpSession);

        // Fetch form details to update
        String currentPassword = updateDetailsForm.getCurrentPassword();
        String newPassword = updateDetailsForm.getNewPassword();
        String confirmNewPassword = updateDetailsForm.getConfirmNewPassword();
        String email = updateDetailsForm.getEmail();

        boolean updatePassword =    (newPassword != null && newPassword.length() > 0) ||
                                    (confirmNewPassword != null && confirmNewPassword.length() > 0);

        boolean updateEmail = (email != null && email.length() > 0 && !email.equals(user.getEmail()));

        // Check current password is correct
        if (!user.getPassword().isValid(authenticationService.getGlobalPasswordSalt(), currentPassword))
        {
            // TODO: add attempts here and auto-logout after x attempts
            bindingResult.rejectValue("currentPassword", "account.current-password");
        }
        // If new password specified, check they match
        else if (updatePassword && !newPassword.equals(confirmNewPassword))
        {
            bindingResult.rejectValue("newPassword", "account.passwords.nomatch");
        }
        else if(!bindingResult.hasErrors())
        {
            // Update password
            if (updatePassword)
            {
                user.setPassword(new Password(authenticationService.getGlobalPasswordSalt(), newPassword));
            }

            // Update e-mail
            if (updateEmail)
            {
                user.setEmail(email);
            }

            redirectAttributes.addFlashAttribute("account_update_success", true);
        }

        return redirectToAccountView(redirectAttributes, bindingResult, "updateDetailsForm", updateDetailsForm);
    }

    @RequestMapping(value = "delete")
    public ModelAndView accountProcessDelete()
    {
        // Set account for deletion

        return redirectToAccountView(null, null, null, null);
    }

    @ModelAttribute("updateDetailsForm")
    public UpdateDetailsForm beanUpdateDetailsForm()
    {
        return new UpdateDetailsForm();
    }

    public ModelAndView redirectToAccountView(RedirectAttributes redirectAttributes, BindingResult bindingResult,
                                              String formName, Object formData)
    {
        ModelAndView modelAndView = new ModelAndView("redirect:/account");

        // Attach binding result
        if (redirectAttributes != null && bindingResult != null && formName != null)
        {
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + formName, bindingResult);
            redirectAttributes.addFlashAttribute(formName, formData);
        }

        return modelAndView;
    }

}
