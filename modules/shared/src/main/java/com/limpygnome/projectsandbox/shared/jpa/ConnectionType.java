package com.limpygnome.projectsandbox.shared.jpa;

/**
 * Created by limpygnome on 26/04/15.
 */
public enum ConnectionType
{
    PROJECTSANDBOX("projectsandbox")
    ;

    public final String PERSISTENCE_UNIT;

    ConnectionType(String PERSISTENCE_UNIT)
    {
        this.PERSISTENCE_UNIT = PERSISTENCE_UNIT;
    }
}
