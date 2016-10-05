package com.projectsandbox.components.server.world.map;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.effect.EffectsMapData;
import com.projectsandbox.components.server.entity.EntityMapData;
import com.projectsandbox.components.server.entity.ai.ArtificialIntelligenceMapData;
import com.projectsandbox.components.server.entity.respawn.RespawnMapData;
import com.projectsandbox.components.server.network.packet.OutboundPacket;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a (world) map, an environment/area in which a player interacts.
 */
public abstract class WorldMap
{
    /**
     * Cached packet of data sent to client for map.
     *
     * WARNING: if this is used elsewhere, it needs thread protection.
     */
    protected transient OutboundPacket packet;

    // Mandatory and not stored as generic map data
    private String mapId;

    // Map data for services
    @Autowired
    private GeneralMapData generalMapData;
    @Autowired
    private EntityMapData entityMapData;
    @Autowired
    private RespawnMapData respawnMapData;
    @Autowired
    private EffectsMapData effectsMapData;
    @Autowired
    private ArtificialIntelligenceMapData artificialIntelligenceMapData;

    /**
     * Creates a new instance and sets up internal state ready for tile data.
     */
    public WorldMap(String mapId, Controller controller)
    {
        this.mapId = mapId;
        controller.inject(this);
    }

    /**
     * Retrieves the identifier for this map.
     *
     * @return unique identifier
     */
    public String getMapId()
    {
        return mapId;
    }

    /**
     * Sets the map up for runtime.
     *
     * This should be done post loading map properties and data.
     */
    public void postMapLoad()
    {
        entityMapData.reset(this);
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

    /**
     * @return the maximum x axis for this map
     */
    public abstract float getMaxX();

    /**
     * @return he maximum y axis for this map
     */
    public abstract float getMaxY();

    public GeneralMapData getGeneralMapData()
    {
        return generalMapData;
    }

    public EntityMapData getEntityMapData()
    {
        return entityMapData;
    }

    public RespawnMapData getRespawnMapData()
    {
        return respawnMapData;
    }

    public EffectsMapData getEffectsMapData()
    {
        return effectsMapData;
    }

    public ArtificialIntelligenceMapData getArtificialIntelligenceMapData()
    {
        return artificialIntelligenceMapData;
    }

    public List<MapData> getMapData()
    {
        List<MapData> result = new LinkedList<>();
        result.add(generalMapData);
        result.add(entityMapData);
        result.add(respawnMapData);
        result.add(effectsMapData);
        result.add(artificialIntelligenceMapData);
        return result;
    }

    @Override
    public String toString()
    {
        return "WorldMap{" +
                "packet=" + packet +
                ", entityMapData=" + entityMapData +
                ", generalMapData=" + generalMapData +
                ", respawnMapData=" + respawnMapData +
                ", effectsMapData=" + effectsMapData +
                ", artificialIntelligenceMapData=" + artificialIntelligenceMapData +
                '}';
    }

}
