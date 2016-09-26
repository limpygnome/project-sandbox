package com.projectsandbox.components.server.world.map;

/**
 * Used to contain properties belonging to an instance of {@link WorldMap}.
 */
public class WorldMapProperties
{
    private String name;
    private boolean lobby;
    private boolean enabled;
    private String defaultEntityTypeName;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isLobby()
    {
        return lobby;
    }

    public void setLobby(boolean lobby)
    {
        this.lobby = lobby;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getDefaultEntityTypeName()
    {
        return defaultEntityTypeName;
    }

    public void setDefaultEntityTypeName(String defaultEntityType)
    {
        this.defaultEntityTypeName = defaultEntityType;
    }

    @Override
    public String toString()
    {
        return "WorldMapProperties{" +
                "name='" + name + '\'' +
                ", lobby=" + lobby +
                ", enabled=" + enabled +
                ", defaultEntityTypeName=" + defaultEntityTypeName +
                '}';
    }

}
