package com.projectsandbox.components.website.model.form.home;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.projectsandbox.components.website.constant.AccountConstants.NICKNAME_LENGTH_MIN;
import static com.projectsandbox.components.website.constant.AccountConstants.PASSWORD_LENGTH_MIN;

/**
 * Created by limpygnome on 22/07/15.
 */
public class LoginForm
{
    @NotNull
    @Size(min = NICKNAME_LENGTH_MIN, message = "{login.incorrect}")
    private String nickname;

    @NotNull
    @Size(min = PASSWORD_LENGTH_MIN, message = "{login.incorrect}")
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
