package com.limpygnome.projectsandbox.website.model.form.home;

/**
 * Created by limpygnome on 22/07/15.
 */
public class LoginForm
{
    private String nickname;
    private String password;

    public LoginForm() { }

    public LoginForm(String nickname, String password)
    {
        this.nickname = nickname;
        this.password = password;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
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
