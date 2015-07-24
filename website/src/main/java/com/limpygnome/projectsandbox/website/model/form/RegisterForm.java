package com.limpygnome.projectsandbox.website.model.form;

import com.limpygnome.projectsandbox.website.validation.annotation.Username;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by limpygnome on 22/07/15.
 */
public class RegisterForm
{
    @NotNull
    @Username(message = "{register.username.chars}")
    @Size(min = 1, max = 14, message = "{register.username.length}")
    private String username;

    @NotNull
    @Size(min = 4, max = 32, message = "{register.password.length}")
    private String password;

    @NotNull
    @Size(min = 6, max = 50, message = "{register.email.length}")
    private String email;

    public RegisterForm() { }

    public RegisterForm(String username, String password, String email)
    {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
