package com.limpygnome.projectsandbox.server.world.map.loader;

import com.limpygnome.projectsandbox.server.world.map.Map;
import com.limpygnome.projectsandbox.server.world.map.data.MapDataReader;

import java.util.UUID;

/**
 * Used to load a map from a data-source.
 */
public interface MapLoader
{

    Map load(MapDataReader mapDataReader, UUID mapId);

}
