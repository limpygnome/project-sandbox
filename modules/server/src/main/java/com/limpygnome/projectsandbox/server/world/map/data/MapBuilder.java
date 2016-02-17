package com.limpygnome.projectsandbox.server.world.map.data;

import com.limpygnome.projectsandbox.server.world.spawn.Spawn;
import com.limpygnome.projectsandbox.server.world.tile.TileType;
import org.json.simple.JSONArray;

/**
 * A reader for primitive map data to be used in building a map.
 *
 * Allows common primitives for e.g. entities to be converted into actual entities placed in the map.
 */
public interface MapBuilder
{

    void addTiles(TileType[] tileTypes, JSONArray tileData);

    void addEntity(String typeName, String typeId, short faction, Spawn spawn);

    void addFactionSpawn(short factionId, Spawn spawn);

}
