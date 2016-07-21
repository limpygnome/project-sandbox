package com.projectsandbox.components.server.world.map;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.effect.EffectsManager;
import com.projectsandbox.components.server.effect.EffectsMapData;
import com.projectsandbox.components.server.entity.EntityManager;
import com.projectsandbox.components.server.entity.respawn.RespawnManager;
import com.projectsandbox.components.server.entity.ai.ArtificialIntelligenceManager;
import com.projectsandbox.components.server.entity.respawn.RespawnMapData;
import com.projectsandbox.components.server.network.packet.OutboundPacket;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a (world) map, an environment/area in which a player interacts.
 */
public abstract class WorldMap implements Serializable
{
    private static final long serialVersionUID = 1L;

    protected transient final Controller controller;
    protected transient final MapService mapService;

    // TODO: services need to send only data to players in map; thus we'll need to map players to maps i.e. broadcastToMap
    // TODO: consider making protected...


    // TODO: need to move map-dependent data out of managers, move to global level and then data back into here...
    public EntityManager entityManager;
    private RespawnMapData respawnMapData;
    private EffectsMapData effectsMapData;
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
    protected WorldMapProperties properties;

    /**
     * Cached packet of data sent to client for map.
     *
     * WARNING: if this is used elsewhere, it needs thread protection.
     */
    protected transient OutboundPacket packet;

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
        this.entityManager = new EntityManager(controller, this);
        this.respawnMapData = new RespawnMapData();
        this.effectsMapData = new EffectsMapData();
        this.artificialIntelligenceManager = new ArtificialIntelligenceManager(controller, this);
    }

    /**
     * Sets the map up for runtime.
     *
     * This should be done post loading map properties and data.
     */
    public void postMapLoad()
    {
        this.entityManager.postMapLoad();
    }

    public void logic()
    {
        // Execute manager logic...
        entityManager.logic();
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

    /**
     * Retrieves the packet used
     * @return
     */
    public OutboundPacket getPacket()
    {
        return packet;
    }

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

    /**
     * @return the maximum x axis for this map
     */
    public abstract float getMaxX();

    /**
     * @return he maximum y axis for this map
     */
    public abstract float getMaxY();

    public RespawnMapData getRespawnMapData()
    {
        return respawnMapData;
    }

    public EffectsMapData getEffectsMapData()
    {
        return effectsMapData;
    }

    @Override
    public String toString()
    {
        // TODO: regenerate this / move into imp
        StringBuilder sb = new StringBuilder();

        sb      .append("map{properties:\n")
                .append(properties.toString())
                .append("\n,\ntile data:\n")
                .append("\n}");
        
        return sb.toString();
    }
    
}
