package com.projectsandbox.components.server.map.repository.file.type;

import com.projectsandbox.components.server.Controller;
import com.projectsandbox.components.server.map.repository.file.FileSystemGenericWoldMapBuilder;
import com.projectsandbox.components.server.world.map.MapService;
import com.projectsandbox.components.server.world.map.WorldMap;
import com.projectsandbox.components.server.world.map.type.open.OpenWorldMap;
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

}
