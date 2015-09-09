package com.limpygnome.projectsandbox.server.world.map.loader;

import com.limpygnome.projectsandbox.server.world.map.Map;
import com.limpygnome.projectsandbox.server.world.map.data.MapDataReader;

import java.util.UUID;

/**
 * Created by limpygnome on 09/09/15.
 */
public class FileSystemLoader implements MapLoader
{

    @Override
    public Map load(MapDataReader mapDataReader, UUID mapId)
    {
        // Map ID is currently ignored...
    }
}
