package com.limpygnome.projectsandbox.website.controllers.pages.main;

import com.limpygnome.projectsandbox.website.controllers.BaseController;
import com.limpygnome.projectsandbox.website.model.form.RegisterForm;
import com.limpygnome.projectsandbox.website.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * Created by limpygnome on 19/04/2015.
 */
@Controller
public class HomeController extends BaseController
{
    @Autowired
    private AuthenticationService authenticationService;

    @ModelAttribute("registerForm")
    public RegisterForm getRegisterForm()
    {
        return new RegisterForm();
    }

    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home()
    {
        ModelAndView modelAndView = createMV("main/home", "welcome", "join");

        // Setup model
        modelAndView.addObject("user", authenticationService.retrieveCurrentUser());

        return modelAndView;
    }

    @RequestMapping(value = {"/home/submit"})
    public ModelAndView homeSubmit(@ModelAttribute("registerForm") @Valid RegisterForm registerForm, BindingResult bindingResult)
    {
        ModelAndView modelAndView = createMV("main/home", "welcome", "join");

        // Setup model
        modelAndView.addObject("user", authenticationService.retrieveCurrentUser());
//        modelAndView.addObject(BindingResult.MODEL_KEY_PREFIX + "registerForm", bindingResult);
//        modelAndView.addObject("registerForm", registerForm);

        return modelAndView;
    }


}
