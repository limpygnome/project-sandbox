package com.limpygnome.projectsandbox.website.controller.page.main;

import com.limpygnome.projectsandbox.shared.jpa.provider.GameProvider;
import com.limpygnome.projectsandbox.shared.model.GameSession;
import com.limpygnome.projectsandbox.shared.model.Password;
import com.limpygnome.projectsandbox.shared.model.User;
import com.limpygnome.projectsandbox.website.controller.BaseController;
import com.limpygnome.projectsandbox.website.model.form.account.UpdateDetailsForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final static Logger LOG = LogManager.getLogger(AccountController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @ModelAttribute("updateDetailsForm")
    public UpdateDetailsForm beanUpdateDetailsForm()
    {
        return new UpdateDetailsForm();
    }

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

    @RequestMapping(value = "delete", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView accountProcessDelete(@RequestParam(value = "confirm", defaultValue = "false", required = false) boolean confirm,
                                             HttpSession httpSession)
    {
        if (!confirm)
        {
            return redirectConfirmView("delete", "confirm account deletion");
        }
        else
        {
            User user = authenticationService.retrieveCurrentUser(httpSession);

            // Destroy session
            authenticationService.logout(httpSession);

            // Delete account
            GameProvider gameProvider = new GameProvider();

            try
            {
                gameProvider.begin();
                gameProvider.removeUser(user);
                gameProvider.commit();

                LOG.info("Deleted user - user id: {}, nickname: {}", user.getUserId(), user.getNickname());
            }
            catch (Exception e)
            {
                LOG.error("Failed to remove user - user id: {}", user.getUserId(), e);
            }
            finally
            {
                gameProvider.close();
            }

            // Redirect to home
            return new ModelAndView("redirect:/home");
        }
    }

    @RequestMapping(value = "reset/game-session", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView accountProcessResetGameSession(@RequestParam(value = "confirm", defaultValue = "false", required = false) boolean confirm,
                                                       HttpSession httpSession)
    {
        if (!confirm)
        {
            return redirectConfirmView("reset-game-session", "confirm reset game session");
        }
        else
        {
            User user = authenticationService.retrieveCurrentUser(httpSession);
            GameProvider gameProvider = new GameProvider();

            try
            {
                // Delete game session
                GameSession gameSession = gameProvider.fetchGameSessionByUser(user);

                if (gameSession == null)
                {
                    LOG.warn("Attempted to delete game session for user, but no game session found - user id: {}", user.getUserId());
                }
                else
                {
                    gameProvider.begin();
                    gameProvider.removeGameSession(gameSession);
                    gameProvider.commit();

                    LOG.info("Deleted game session - token: {}, user id: {}", gameSession.getToken(), user.getUserId());
                }
            }
            catch (Exception e)
            {
                gameProvider.rollback();

                LOG.error("Failed to reset game session - user id: {}", user.getUserId());
            }
            finally
            {
                gameProvider.close();
            }

            return redirectToAccountView(null, null, null, null);
        }
    }

    @RequestMapping(value = "reset/stats", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView accountProcessResetStats(@RequestParam(value = "confirm", defaultValue = "false", required = false) boolean confirm,
                                                 HttpSession httpSession)
    {
        if (!confirm)
        {
            return redirectConfirmView("reset-stats", "confirm reset stats");
        }
        else
        {
            // Reset stats for user
            User user = authenticationService.retrieveCurrentUser(httpSession);
            user.getPlayerMetrics().reset();

            // Persist changes
            GameProvider gameProvider = new GameProvider();

            try
            {
                gameProvider.begin();
                gameProvider.updateUser(user);
                gameProvider.commit();

                LOG.info("Reset player stats - user id: {}", user.getUserId());
            }
            catch (Exception e)
            {
                gameProvider.rollback();

                LOG.error("Failed to reset stats for user - user id: {}", user.getUserId(), e);
            }
            finally
            {
                gameProvider.close();
            }

            return redirectToAccountView(null, null, null, null);
        }
    }

    public ModelAndView redirectConfirmView(String subpage, String title)
    {
        ModelAndView modelAndView = createMV("main/account-confirm", title);
        modelAndView.addObject("subpage", subpage);
        return modelAndView;
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
