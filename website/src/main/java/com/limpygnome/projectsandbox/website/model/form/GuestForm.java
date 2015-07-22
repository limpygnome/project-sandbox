package com.limpygnome.projectsandbox.website.model.form;

/**
 * Created by limpygnome on 22/07/15.
 */
public class GuestForm
{
    private String nickname;

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
