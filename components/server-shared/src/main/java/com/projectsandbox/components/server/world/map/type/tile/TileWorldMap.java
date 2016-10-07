package com.projectsandbox.components.server.world.map.type.tile;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.mapdata.MapData;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.type.MapType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * A world map where entities move along tiles.
 */
@MapType(typeName = "tile-world-map", classType = TileWorldMap.class)
public class TileWorldMap extends WorldMap
{
    /**
     * Tile data for this map.
     */
    @Autowired
    public TileMapData tileMapData;

    public TileWorldMap(String mapId, Controller controller)
    {
        super(mapId, controller);
    }

    @Override
    public void rebuildMapPacket() throws IOException
    {
        TileMapDataOutboundPacket packet = new TileMapDataOutboundPacket();
        packet.build(this);

        this.packet = packet;
    }

    @Override
    public float getMaxX()
    {
        return tileMapData.maxX;
    }

    @Override
    public float getMaxY()
    {
        return tileMapData.maxY;
    }

    @Override
    public List<MapData> getMapData()
    {
        List<MapData> result = super.getMapData();
        result.add(tileMapData);
        return result;
    }

}
