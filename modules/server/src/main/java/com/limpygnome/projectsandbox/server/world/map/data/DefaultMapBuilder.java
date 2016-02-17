package com.limpygnome.projectsandbox.server.world.map.data;

import com.limpygnome.projectsandbox.server.world.spawn.Spawn;
import com.limpygnome.projectsandbox.server.world.tile.TileType;
import org.json.simple.JSONArray;

/**
 * Default implementation.
 */
public class DefaultMapBuilder implements MapBuilder
{

    @Override
    public void addTiles(TileType[] tileTypes, JSONArray tileData)
    {
    }

    @Override
    public void addEntity(String typeName, String typeId, short faction, Spawn spawn)
    {
    }

    @Override
    public void addFactionSpawn(short factionId, Spawn spawn)
    {
    }

}
