package com.projectsandbox.server.map.repository.file.load.type;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.WorldMapProperties;
import com.projectsandbox.components.server.world.map.type.open.OpenWorldMap;
import com.projectsandbox.components.server.world.map.type.open.OpenWorldMapProperties;
import com.projectsandbox.server.map.repository.file.load.FileSystemGenericWoldMapBuilder;
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


    }

}
