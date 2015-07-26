package com.limpygnome.projectsandbox.website.constant;

/**
 * Created by limpygnome on 26/07/15.
 */
public class SessionConstants
{
    private SessionConstants() { }

    /**
     * The initial delay before purging inactive sessions.
     */
    public static final long PURGE_SESSIONS_INITIAL_DELAY_MS = 0;

    /**
     * The interval between purging inactive sessions.
     */
    public static final long PURGE_SESSIONS_INTERVAL_MS = 30000;

}
