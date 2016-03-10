package com.limpygnome.projectsandbox.server.world.map.repository.file;

import com.limpygnome.projectsandbox.server.Controller;
import com.limpygnome.projectsandbox.server.world.map.MapService;
import com.limpygnome.projectsandbox.server.world.map.WorldMap;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * Implemented by different types of maps to construct an instance from JSON map data.
 *
 * Implementations should be thread-safe.
 */
public interface FileSystemMapBuilder
{

    /**
     * Builds an instance from the provided map data.
     *
     * @param controller
     * @param mapService
     * @param mapData
     * @return
     */
    WorldMap build(Controller controller, MapService mapService, JSONObject mapData) throws IOException

}
