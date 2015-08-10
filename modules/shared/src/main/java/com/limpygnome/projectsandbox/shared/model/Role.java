package com.limpygnome.projectsandbox.shared.model;

/**
 * Represents a role of a user.
 *
 * The highest role belonging to a role set / user dictates the user's primary role.
 *
 * Note: a user can belong to multiple roles.
 */
public enum Role
{
    USER(1, "User"),
    MODERATOR(100, "Moderator"),
    ADMINISTRATOR(500, "Administrator"),
    BANNED(9999, "Banned");

    public final int PRIORITY;
    public final String DISPLAY_NAME;

    Role(int PRIORITY, String DISPLAY_NAME)
    {
        this.PRIORITY = PRIORITY;
        this.DISPLAY_NAME = DISPLAY_NAME;
    }

}
