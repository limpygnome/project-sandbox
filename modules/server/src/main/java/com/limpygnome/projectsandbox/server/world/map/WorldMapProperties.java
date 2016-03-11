package com.limpygnome.projectsandbox.server.world.map;

/**
 * Used to contain properties belonging to an instance of {@link WorldMap}.
 */
public class WorldMapProperties
{
    private String name;
    private boolean lobby;

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

    @Override
    public String toString()
    {
        return "WorldMapProperties{" +
                "name='" + name + '\'' +
                ", lobby=" + lobby +
                '}';
    }

}
