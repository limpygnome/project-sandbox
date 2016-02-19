package com.limpygnome.projectsandbox.server.world.map;

/**
 * Used to contain properties belonging to an instance of {@link WorldMap}.
 */
public final class WorldMapProperties
{

    public String name;
    public boolean lobby;


    @Override
    public String toString()
    {
        return "properties{ name: " + name + ", lobby: " + lobby + "}";
    }

}
