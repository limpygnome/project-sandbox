package com.limpygnome.projectsandbox.shared.model;

import com.limpygnome.projectsandbox.shared.util.DateTimeUtil;
import org.hibernate.annotations.Type;
import org.joda.time.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Period;
import java.util.UUID;

/**
 * Created by limpygnome on 25/07/15.
 */
@Entity
@Cacheable(false)
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"userid", "nickname", "email"}))
public class User implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "userid", nullable = false)
    private String userId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "registered", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime registered;

    @Embedded
    private Password password;

    @Embedded
    private PlayerMetrics playerMetrics;

    @Embedded
    private Roles roles;

    public User()
    {
        this.userId = null;
        this.nickname = null;
        this.email = null;
        this.password = null;
        this.playerMetrics = new PlayerMetrics();
        this.roles = new Roles();
    }

    public User(String nickname, String email, String globalPasswordSalt, String password)
    {
        this.nickname = nickname;
        this.email = email;
        this.password = new Password(globalPasswordSalt, password);

        // Default values
        this.userId = UUID.randomUUID().toString();
        this.playerMetrics = new PlayerMetrics();
        this.registered = DateTime.now();
        this.roles = new Roles();
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
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

    public Password getPassword()
    {
        return password;
    }

    public void setPassword(Password password)
    {
        this.password = password;
    }

    public PlayerMetrics getPlayerMetrics()
    {
        return playerMetrics;
    }

    public Roles getRoles()
    {
        return roles;
    }

    public DateTime getRegistered()
    {
        return registered;
    }

    public String getRegisteredHuman()
    {
        return DateTimeUtil.humanTimeSince(registered, DateTime.now());
    }

}
