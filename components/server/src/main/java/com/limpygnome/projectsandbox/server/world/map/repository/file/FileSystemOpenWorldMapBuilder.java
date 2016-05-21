package com.limpygnome.projectsandbox.server.world.map.repository.file;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import com.limpygnome.projectsandbox.server.world.map.WorldMapProperties;
import com.limpygnome.projectsandbox.server.world.map.type.open.OpenWorldMap;
import com.limpygnome.projectsandbox.server.world.map.type.open.OpenWorldMapProperties;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Implementation for {@link OpenWorldMap}.
 */
@Component
public class FileSystemOpenWorldMapBuilder extends FileSystemGenericWoldMapBuilder
{

    @Override
    public String getBuilderName()
    {
        return "open-world-map";
    }

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

        if (rawProperties == null)
        {
            throw new RuntimeException("No properties section found in map file");
        }

        // Read custom properties for this map
        OpenWorldMapProperties properties = (OpenWorldMapProperties) map.getProperties();

        properties.setBackground((String) rawProperties.get("background"));
        properties.setLimitWidth((float) (double) rawProperties.get("width"));
        properties.setLimitHeight((float) (double) rawProperties.get("height"));
    }

}
