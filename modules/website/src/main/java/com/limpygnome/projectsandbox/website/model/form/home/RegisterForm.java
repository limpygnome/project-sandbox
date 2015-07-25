package com.limpygnome.projectsandbox.website.model.form.home;

import com.limpygnome.projectsandbox.website.validation.annotation.NicknameChars;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.limpygnome.projectsandbox.website.constant.AccountConstants.NICKNAME_LENGTH_MIN;
import static com.limpygnome.projectsandbox.website.constant.AccountConstants.NICKNAME_LENGTH_MAX;
import static com.limpygnome.projectsandbox.website.constant.AccountConstants.PASSWORD_LENGTH_MIN;
import static com.limpygnome.projectsandbox.website.constant.AccountConstants.PASSWORD_LENGTH_MAX;
import static com.limpygnome.projectsandbox.website.constant.AccountConstants.EMAIL_LENGTH_MIN;
import static com.limpygnome.projectsandbox.website.constant.AccountConstants.EMAIL_LENGTH_MAX;

/**
 * Created by limpygnome on 22/07/15.
 */
public class RegisterForm
{
    @NotNull(message = "{nickname.length}")
    @NicknameChars(message = "{nickname.format}")
    @Size(min = NICKNAME_LENGTH_MIN, max = NICKNAME_LENGTH_MAX, message = "{nickname.length}")
    private String nickname;

    @NotNull(message = "{password.length}")
    @Size(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX, message = "{password.length}")
    private String password;

    @NotNull(message = "{email.length}")
    @Size(min = EMAIL_LENGTH_MIN, max = EMAIL_LENGTH_MAX, message = "{email.length}")
    private String email;

    public RegisterForm() { }

    public RegisterForm(String nickname, String password, String email)
    {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
