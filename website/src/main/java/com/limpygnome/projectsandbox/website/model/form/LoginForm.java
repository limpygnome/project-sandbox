package com.limpygnome.projectsandbox.website.model.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by limpygnome on 22/07/15.
 */
public class LoginForm
{
    @NotNull
    @Size(min = 1, max = 14)
    private String username;

    @NotNull
    @Size(min = 4, max = 32)
    private String password;

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
