package com.projectsandbox.components.website.model.form.account;

import com.projectsandbox.components.website.validation.annotation.Email;
import com.projectsandbox.components.website.validation.annotation.SizeWhenSpecified;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.projectsandbox.components.website.constant.AccountConstants.EMAIL_LENGTH_MIN;
import static com.projectsandbox.components.website.constant.AccountConstants.EMAIL_LENGTH_MAX;
import static com.projectsandbox.components.website.constant.AccountConstants.PASSWORD_LENGTH_MIN;
import static com.projectsandbox.components.website.constant.AccountConstants.PASSWORD_LENGTH_MAX;

/**
 * Used by account page to update account details.
 */
public class UpdateDetailsForm
{
    @NotNull(message = "{account.current-password}")
    private String currentPassword;

    @NotNull(message = "{password.length}")
    @SizeWhenSpecified(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    private String newPassword;

    @NotNull(message = "{password.length}")
    private String confirmNewPassword;

    @NotNull(message = "{email.length}")
    @Size(min = EMAIL_LENGTH_MIN, max = EMAIL_LENGTH_MAX, message = "{email.length}")
    @Email(message = "{email.format}")
    private String email;

    public UpdateDetailsForm() { }

    public String getCurrentPassword()
    {
        return currentPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public String getConfirmNewPassword()
    {
        return confirmNewPassword;
    }

    public String getEmail()
    {
        return email;
    }

    public void setCurrentPassword(String currentPassword)
    {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword)
    {
        this.confirmNewPassword = confirmNewPassword;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

}
