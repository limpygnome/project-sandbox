package com.limpygnome.projectsandbox.shared.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by limpygnome on 25/07/15.
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"userid", "nickname", "email"}))
public class User implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "userid", nullable = false)
    private UUID userId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    public User()
    {
        this.userId = null;
        this.nickname = null;
        this.email = null;
        this.passwordHash = null;
        this.passwordSalt = null;
    }

    public UUID getUserId()
    {
        return userId;
    }

    public void setUserId(UUID userId)
    {
        this.userId = userId;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt()
    {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt)
    {
        this.passwordSalt = passwordSalt;
    }

}
