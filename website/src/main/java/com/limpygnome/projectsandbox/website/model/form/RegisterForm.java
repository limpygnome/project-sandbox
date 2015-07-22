package com.limpygnome.projectsandbox.website.model.form;

/**
 * Created by limpygnome on 22/07/15.
 */
public class RegisterForm
{
    private String username;
    private String password;

    private String email;

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
