package com.projectsandbox.components.shared.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by limpygnome on 29/07/15.
 */
@Embeddable
public class Roles implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "userid"))
    @Column(name = "role", nullable = false)
    private Set<Role> roles;

    public Roles()
    {
        this.roles = new HashSet<>();
    }

    public synchronized void add(Role role)
    {
        // Ignore user role, the user always has this role
        if (role != Role.USER)
        {
            roles.add(role);
        }
    }

    public synchronized boolean remove(Role role)
    {
        return roles.remove(role);
    }

    public synchronized boolean contains(Role role)
    {
        if (role == Role.USER)
        {
            return true;
        }
        else
        {
            return roles.contains(role);
        }
    }

    public synchronized boolean containsTag(String roles)
    {
        String[] rawRolesArr = roles.split(",");

        Role parsedRole;
        for (String rawRole : rawRolesArr)
        {
            // Parse into enum
            parsedRole = Role.valueOf(rawRole.trim().toUpperCase());

            // Check if it exists
            if (this.roles.contains(parsedRole))
            {
                return true;
            }
        }

        return false;
    }

    public synchronized Role getPrimaryRole()
    {
        Role highestRole = Role.USER;

        for (Role role : roles)
        {
            if (highestRole.PRIORITY <= role.PRIORITY)
            {
                highestRole = role;
            }
        }

        return highestRole;
    }

}
