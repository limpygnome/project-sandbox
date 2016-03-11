package com.limpygnome.projectsandbox.server.world.map.repository.file;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.WorldMapProperties;
import com.limpygnome.projectsandbox.server.world.map.open.OpenWorldMap;
import com.limpygnome.projectsandbox.server.world.map.open.OpenWorldMapProperties;
import org.json.simple.JSONObject;

import javax.annotation.Resource;

/**
 * Implementation for {@link OpenWorldMap}.
 */
@Resource(name = "open-world-map")
public class FileSystemOpenWorldMapBuilder extends FileSystemGenericWoldMapBuilder
{

    @Override
    public WorldMap createMapInstance(Controller controller, MapService mapService, short mapId)
    {
        return new OpenWorldMap(controller, mapService, mapId);
    }

    @Override
    public WorldMapProperties createPropertiesInstance()
    {
        return new OpenWorldMapProperties();
    }

    @Override
    protected void buildMapProperties(WorldMap map, JSONObject mapData)
    {
        super.buildMapProperties(map, mapData);

        JSONObject rawProperties = (JSONObject) mapData.get("properties");

        // Read custom properties for this map
        OpenWorldMapProperties properties = (OpenWorldMapProperties) map.getProperties();

        properties.setBackground((String) rawProperties.get("background"));
        properties.setLimitWidth((float) rawProperties.get("width"));
        properties.setLimitHeight((float) rawProperties.get("height"));
    }

}
