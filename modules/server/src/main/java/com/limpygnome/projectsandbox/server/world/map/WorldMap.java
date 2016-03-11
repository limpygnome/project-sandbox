package com.limpygnome.projectsandbox.server.world.map;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.effect.EffectsManager;
import com.limpygnome.projectsandbox.server.entity.EntityManager;
import com.limpygnome.projectsandbox.server.entity.RespawnManager;
import com.limpygnome.projectsandbox.server.entity.ai.ArtificialIntelligenceManager;
import com.limpygnome.projectsandbox.server.packet.OutboundPacket;
import com.limpygnome.projectsandbox.server.world.map.packet.TileMapDataOutboundPacket;

import com.limpygnome.projectsandbox.server.world.map.tile.TileData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Represents a (world) map, an environment/area in which a player interacts.
 */
public abstract class WorldMap
{
    protected final Controller controller;
    protected final MapService mapService;

    // TODO: services need to send only data to players in map; thus we'll need to map players to maps i.e. broadcastToMap
    // TODO: consider making protected...
    public EntityManager entityManager;
    public RespawnManager respawnManager;
    public EffectsManager effectsManager;
    public ArtificialIntelligenceManager artificialIntelligenceManager;

    /**
     * Unique identifier for this map.
     *
     * TODO: convert to UUID.
     */
    private final short mapId;

    /**
     * Properties and cached values for this map.
     */
    private WorldMapProperties properties;

    /**
     * Tile data for this map.
     */
    public TileData tileData;

    /**
     * Cached packet of data sent to client for map.
     *
     * WARNING: if this is used elsewhere, it needs thread protection.
     */
    protected OutboundPacket packet;

    /**
     * Creates a new instance and sets up internal state ready for tile data.
     *
     * @param controller
     * @param mapService The map manager to which this instance belongs
     * @param mapId The unique identifier for this map
     */
    public WorldMap(Controller controller, MapService mapService, short mapId)
    {
        this.controller = controller;
        this.mapService = mapService;
        this.mapId = mapId;

        // Setup managers
        // TODO: either pass or remove references to controller for managers
        this.entityManager = new EntityManager(controller, this);
        this.respawnManager = new RespawnManager(controller, this);
        this.effectsManager = new EffectsManager(controller);
        this.artificialIntelligenceManager = new ArtificialIntelligenceManager(controller, this);
    }

    public void logic()
    {
        // Execute manager logic...
        entityManager.logic();
        respawnManager.logic();
        effectsManager.logic();
    }

    /**
     * Used to update the internal packet used to represent the map.
     *
     * The packet should be reusable, so that it can be sent to multiple players. This may occur before the serber has
     * actually finished starting up, thus implementations shouldn't rely on other dependencies.
     *
     * @throws IOException thrown if the packet cannot be constructed
     */
    public abstract void rebuildMapPacket() throws IOException;

    public short getMapId()
    {
        return mapId;
    }

    public void setProperties(WorldMapProperties properties)
    {
        this.properties = properties;
    }

    public WorldMapProperties getProperties()
    {
        return properties;
    }

    @Override
    public String toString()
    {
        // TODO: regenerate this / move into imp
        StringBuilder sb = new StringBuilder();

        sb      .append("map{properties:\n")
                .append(properties.toString())
                .append("\n,\ntile data:\n")
                .append(tileData.toString())
                .append("\n}");
        
        return sb.toString();
    }
    
}
