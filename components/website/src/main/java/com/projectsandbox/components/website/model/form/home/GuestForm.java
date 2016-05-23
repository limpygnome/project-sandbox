package com.projectsandbox.components.website.model.form.home;

import com.projectsandbox.components.website.validation.annotation.NicknameChars;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.projectsandbox.components.website.constant.AccountConstants.NICKNAME_LENGTH_MAX;
import static com.projectsandbox.components.website.constant.AccountConstants.NICKNAME_LENGTH_MIN;

/**
 * Created by limpygnome on 22/07/15.
 */
public class GuestForm
{
    @NotNull(message = "{nickname.length}")
    @NicknameChars(message = "{nickname.format}")
    @Size(min = NICKNAME_LENGTH_MIN, max = NICKNAME_LENGTH_MAX, message = "{nickname.length}")
    private String nickname;

    public GuestForm() { }

    public GuestForm(String nickname)
    {
        this.nickname = nickname;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }
}
