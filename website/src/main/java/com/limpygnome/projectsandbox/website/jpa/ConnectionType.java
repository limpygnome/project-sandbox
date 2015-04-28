package com.limpygnome.projectsandbox.website.jpa;

/**
 * Created by limpygnome on 26/04/15.
 */
public enum ConnectionType
{
    PROJECTSANDBOX_READONLY("projectsandbox_readonly"),
    PROJECTSANDBOX_FULL("projectsandbox_full")
    ;

    public final String PERSISTENCE_UNIT;

    ConnectionType(String PERSISTENCE_UNIT)
    {
        this.PERSISTENCE_UNIT = PERSISTENCE_UNIT;
    }
}
