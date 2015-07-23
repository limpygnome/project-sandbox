package com.limpygnome.projectsandbox.website.model.form;

import javax.validation.constraints.NotNull;

/**
 * Created by limpygnome on 22/07/15.
 */
public class LoginForm
{
    @NotNull
    private String username;

    @NotNull
    private String password;

    public LoginForm() { }

    public LoginForm(String username, String password)
    {
        this.username = username;
        this.password = password;
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
}
