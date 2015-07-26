package com.limpygnome.projectsandbox.shared.constant;

/**
 * Created by limpygnome on 26/07/15.
 */
public final class SessionConstants
{
    private SessionConstants() { }

    /**
     * The maximum seconds between a session being created and a user connecting to the actual server,
     * before the session is deleted.
     *
     * Protects against users abandoning sessions before connecting to the actual server.
     */
    public static final int TIMEOUT_INITIAL_CONNECTION_SECONDS = 30;

    /**
     * The maximum seconds, since a connected session was updated, before it's considered inactivve and deleted.
     *
     * Protects against the server not correctly disposing sessions.
     */
    public static final int TIMEOUT_LAST_UPDATED_SECONDS = 3600;

}
