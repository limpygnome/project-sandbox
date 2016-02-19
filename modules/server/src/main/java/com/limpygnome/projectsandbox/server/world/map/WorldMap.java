package com.limpygnome.projectsandbox.server.world.map;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.entity.physics.Vector2;
import com.limpygnome.projectsandbox.server.entity.physics.Vertices;
import com.limpygnome.projectsandbox.server.entity.respawn.pending.EntityPendingRespawn;
import com.limpygnome.projectsandbox.server.packet.imp.map.MapDataOutboundPacket;
import com.limpygnome.projectsandbox.server.entity.Entity;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.UUID;

import com.limpygnome.projectsandbox.server.world.spawn.FactionSpawns;
import com.limpygnome.projectsandbox.server.world.spawn.Spawn;
import com.limpygnome.projectsandbox.server.world.tile.TileType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Represents a (world) map, an environment/area in which a player interacts.
 */
public class WorldMap
{
    private final static Logger LOG = LogManager.getLogger(WorldMap.class);

    private final Controller controller;
    private final MapManager mapManager;

    /**
     * Unique identifier for this map.
     *
     * TODO: convert to UUID.
     */
    public short mapId;

    /**
     * Properties and cached values for this map.
     */
    public WorldMapProperties properties;

    /**
     * Tile data for this map.
     */
    public WorldMapTileData tileData;

    /**
     * Cached packet of data sent to client for map.
     *
     * WARNING: if this is used elsewhere, it needs thread protection.
     */
    public MapDataOutboundPacket packet;

    /**
     * Creates a new instance and sets up internal state ready for tile data.
     *
     * @param controller
     * @param mapManager The map manager to which this instance belongs
     * @param mapId The unique identifier for this map
     */
    public WorldMap(Controller controller, MapManager mapManager, short mapId)
    {
        this.controller = controller;
        this.mapManager = mapManager;
        this.mapId = mapId;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb      .append("map{properties:\n")
                .append(properties.toString())
                .append("\n,\ntile data:\n")
                .append(tileData.toString())
                .append("\n}");
        
        return sb.toString();
    }
    
}
