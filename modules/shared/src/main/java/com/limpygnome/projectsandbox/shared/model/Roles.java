package com.limpygnome.projectsandbox.shared.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by limpygnome on 29/07/15.
 */
@Embeddable
public class Roles
{

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "userid"))
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

}